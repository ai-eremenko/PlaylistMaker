package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

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

    private lateinit var searchHistory: SearchHistory
    private lateinit var historyRecyclerView: RecyclerView


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

        searchHistory = SearchHistory(getSharedPreferences("app_prefs", MODE_PRIVATE))
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



        trackAdapter = TrackAdapter(emptyList(), { track ->
            onTrackClicked(track)})
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

            if (searchHistory.getHistory().isNotEmpty()) {
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

        trackAdapter = TrackAdapter(emptyList(), { track ->
            onTrackClicked(track)})
        trackList.layoutManager = LinearLayoutManager(this)
        trackList.adapter = trackAdapter
        trackAdapter.updateTracks(emptyList())
        val itunesApiService = retrofit.create<ItunesApiService>()

        trackList.isGone = true
        noInternetLayout.isGone = true
        noResultsLayout.isGone = true
        progressBar.isGone = false

        itunesApiService.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {

                trackList.isGone = true
                noInternetLayout.isGone = true
                noResultsLayout.isGone = true
                progressBar.isGone = true

                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.resultCount > 0) {
                        trackAdapter.updateTracks(searchResponse.results)
                        trackList.isGone = false
                        noInternetLayout.isGone = true
                        noResultsLayout.isGone = true
                        resultsHistoryLayout.isGone= true
                        progressBar.isGone = true
                    } else {
                        trackList.isGone = true
                        noResultsLayout.isGone = false
                        noInternetLayout.isGone = true
                        resultsHistoryLayout.isGone= true
                        progressBar.isGone = true
                    }
                } else {
                    trackList.isGone = true
                    noResultsLayout.isGone = true
                    noInternetLayout.isGone = false
                    resultsHistoryLayout.isGone= true
                    progressBar.isGone = true
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                trackList.isGone = true
                noResultsLayout.isGone = true
                noInternetLayout.isGone = false
                resultsHistoryLayout.isGone = true
                progressBar.isGone = true
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
            searchHistory.addTrack(track)
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
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            resultsHistoryLayout.isVisible = true
            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            trackAdapter = TrackAdapter(history, { track -> onTrackClicked(track) })
            historyRecyclerView.adapter = trackAdapter
        } else {
            resultsHistoryLayout.isVisible = false
        }
    }

    private fun clearHistory() {
        searchHistory.clearHistory()
        trackAdapter.updateTracks(emptyList())
        resultsHistoryLayout.visibility = View.GONE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
