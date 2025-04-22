package com.example.playlistmaker.presentation.search_track

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.audio_player.AudioPlayerActivity
import com.example.playlistmaker.app.Constants
import com.example.playlistmaker.R
import com.example.playlistmaker.data.local.SearchHistoryStorageImpl
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.extensions.isGone
import com.example.playlistmaker.presentation.extensions.isVisible
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var currentQuery: String? = null
    private val searchRunnable = Runnable {
        currentQuery?.let { query -> performSearch(query) }
    }

    private lateinit var inputEditText: EditText
    private var inputText: String? = null
    private lateinit var backButton: ImageView
    private lateinit var clearIcon: ImageView
    private lateinit var clearHistory: Button

    private lateinit var trackList: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var updateButton: Button
    private var lastSearchQuery: String? = null

    private lateinit var noResultsLayout : ConstraintLayout
    private lateinit var noInternetLayout : ConstraintLayout
    private lateinit var resultsHistoryLayout : LinearLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var historyRecyclerView: RecyclerView

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        resultsHistoryLayout = findViewById(R.id.resultsHistoryLayout)
        backButton = findViewById(R.id.back_button)
        clearHistory = findViewById(R.id.clear_history)
        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        trackList = findViewById(R.id.trackList)
        updateButton = findViewById(R.id.update_button)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        noResultsLayout= findViewById(R.id.noResultsLayout)
        progressBar=findViewById(R.id.progressBar)

        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        setupHistory()

        backButton.setOnClickListener {
            finish()
        }

        clearHistory.setOnClickListener {
            clearHistory()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                currentQuery = inputText
                clearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                updateUI()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)

        savedInstanceState?.let {
            inputText = it.getString(Constants.SEARCH_TEXT_KEY)
            inputEditText.setText(inputText)
        }

        clearIcon.setOnClickListener {
            inputEditText.text.clear()
            inputEditText.clearFocus()
            trackList.isGone = true
            clearIcon.visibility = View.INVISIBLE

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            updateUI()
        }

        trackList.isGone = true
        noInternetLayout.isGone = true
        noResultsLayout.isGone = true
        progressBar.isGone = true



        trackAdapter = TrackAdapter(emptyList(), ::onTrackClicked)
        trackList.layoutManager = LinearLayoutManager(this)
        trackList.adapter = trackAdapter


        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =  getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                inputEditText.clearFocus()
                if (inputEditText.text.isNullOrEmpty()) {
                    updateUI()
                } else {
                    performSearch(inputEditText.text.toString())
                }
                true
            } else {
                false
            }
        }
        updateButton.setOnClickListener {
            lastSearchQuery?.let { query -> performSearch(query) }
        }

    }

    private fun updateUI() {
        if (inputEditText.text.isNullOrEmpty()) {
            trackList.isGone = true
            noResultsLayout.isGone = true
            noInternetLayout.isGone = true

            if (searchHistoryInteractor.getHistory().isNotEmpty()) {
                resultsHistoryLayout.visibility = View.VISIBLE
            } else {
                resultsHistoryLayout.visibility = View.GONE
            }
        } else {
            resultsHistoryLayout.visibility = View.GONE
            trackList.isGone = false
        }
    }

    override fun onResume() {
        super.onResume()
        inputEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun performSearch(query: String) {
        lastSearchQuery = query

        trackAdapter.updateTracks(emptyList())
        trackList.layoutManager = LinearLayoutManager(this)
        trackList.adapter = trackAdapter
        trackAdapter.updateTracks(emptyList())

        trackList.isGone = true
        noInternetLayout.isGone = true
        noResultsLayout.isGone = true
        resultsHistoryLayout.isGone = true
        progressBar.isGone = false


        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {

            runOnUiThread {
                    progressBar.isGone = true

                when {
                    foundTracks.isNotEmpty() -> {
                        trackAdapter.updateTracks(foundTracks)
                        trackList.isGone = false
                        noInternetLayout.isGone = true
                        noResultsLayout.isGone = true
                        resultsHistoryLayout.isGone = true
                    }
                    foundTracks.isEmpty() -> {
                        trackList.isGone = true
                        noInternetLayout.isGone = true
                        noResultsLayout.isGone = false
                        resultsHistoryLayout.isGone = true
                    }
                }
            }
            }

            override fun onError(error: Throwable) {
                runOnUiThread {
                    progressBar.isGone = true
                    trackList.isGone = true
                    noResultsLayout.isGone = true
                    noInternetLayout.isGone = false
                    resultsHistoryLayout.isGone = true
                }
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.SEARCH_TEXT_KEY, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(Constants.SEARCH_TEXT_KEY)
        inputEditText.setText(inputText)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClicked(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor.addTrack(track)
            setupHistory()

            val audioPlayerActivity = Intent(this, AudioPlayerActivity::class.java)
            audioPlayerActivity.putExtra("trackName", track.trackName)
            audioPlayerActivity.putExtra("artistName", track.artistName)
            audioPlayerActivity.putExtra("trackDuration", track.trackTimeMillis)
            audioPlayerActivity.putExtra("artworkUrl", track.artworkUrl100)
            audioPlayerActivity.putExtra("collectionName", track.collectionName)
            audioPlayerActivity.putExtra("releaseDate", track.releaseDate)
            audioPlayerActivity.putExtra("primaryGenreName", track.primaryGenreName)
            audioPlayerActivity.putExtra("country", track.country)
            audioPlayerActivity.putExtra("previewUrl", track.previewUrl)

            startActivity(audioPlayerActivity)
        }
    }

    private fun setupHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            resultsHistoryLayout.isVisible = true
            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            trackAdapter = TrackAdapter(history, ::onTrackClicked)
            historyRecyclerView.adapter = trackAdapter
        } else {
            resultsHistoryLayout.isVisible = false
        }
    }

    private fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        trackAdapter.updateTracks(emptyList())
        resultsHistoryLayout.visibility = View.GONE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
