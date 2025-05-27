package com.example.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.ui.audio_player.activity.AudioPlayerActivity
import com.example.playlistmaker.app.Constants
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.app.extensions.isGone
import com.example.playlistmaker.app.extensions.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.screen_state.SearchScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var inputText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupSearchView()
        setupClickListeners()
        observeViewModel()

        savedInstanceState?.let {
            inputText = it.getString(Constants.SEARCH_TEXT_KEY)
            binding.inputEditText.setText(inputText)
        }
    }

    override fun onResume() {
        super.onResume()
        if (inputText.isNullOrEmpty()) {
            viewModel.loadSearchHistory()
        } else {
            viewModel.searchDebounce(inputText!!)
        }
    }

    private fun setupAdapters() {
        adapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                onTrackClicked(track)
            }
        }

        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                onTrackClicked(track)
            }
        }

        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = adapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupSearchView() {
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s?.toString()
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

                if (s.isNullOrEmpty()) {
                    viewModel.loadSearchHistory()}
                    viewModel.searchDebounce(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.inputEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.inputEditText, InputMethodManager.SHOW_IMPLICIT)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.clearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.resultsHistoryLayout.visibility = View.GONE
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.text.clear()
            hideKeyboard()
            viewModel.loadSearchHistory()
        }

        binding.updateButton.setOnClickListener {
            inputText?.let { query -> viewModel.searchDebounce(query) }
        }
    }

    private fun observeViewModel() {
        viewModel.observeState().observe(this) { state ->
            renderState(state)
        }

        viewModel.observeShowToast().observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderState(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.Content -> showContent(state.tracks)
            is SearchScreenState.Empty -> showEmpty(state.message)
            is SearchScreenState.Error -> showError(state.message)
            is SearchScreenState.History -> showHistory(state.tracks)
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.trackList.isGone = true
        binding.noResultsLayout.isGone = true
        binding.noInternetLayout.isGone = true
        binding.resultsHistoryLayout.isGone = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.isGone = true
        binding.trackList.isVisible = true
        binding.noResultsLayout.isGone = true
        binding.noInternetLayout.isGone = true
        binding.resultsHistoryLayout.isGone = true

        adapter.updateTracks(tracks)
    }

    private fun showEmpty(message: String) {
        binding.progressBar.isGone = true
        binding.trackList.isGone = true
        binding.noResultsLayout.isVisible = true
        binding.noResultsText.text = message
        binding.noInternetLayout.isGone = true
        binding.resultsHistoryLayout.isGone = true
    }

    private fun showError(message: String) {
        binding.progressBar.isGone = true
        binding.trackList.isGone = true
        binding.noResultsLayout.isGone = true
        binding.noInternetLayout.isVisible = true
        binding.resultsHistoryLayout.isGone = true
    }

    private fun showHistory(tracks: List<Track>) {
        binding.progressBar.isGone = true
        binding.trackList.isGone = true
        binding.noResultsLayout.isGone = true
        binding.noInternetLayout.isGone = true

        if (tracks.isNotEmpty()) {
            binding.resultsHistoryLayout.isVisible = true
            historyAdapter.updateTracks(tracks)
        } else {
            binding.resultsHistoryLayout.isVisible = false
        }
    }

    private fun onTrackClicked(track: Track) {
        viewModel.addToHistory(track)
        val audioPlayerActivity = Intent(this, AudioPlayerActivity::class.java)
        audioPlayerActivity.putExtra("trackName", track.trackName)
        audioPlayerActivity.putExtra("artistName", track.artistName)
        audioPlayerActivity.putExtra("trackDuration", track.trackTime)
        audioPlayerActivity.putExtra("artworkUrl", track.artworkUrl100)
        audioPlayerActivity.putExtra("collectionName", track.collectionName)
        audioPlayerActivity.putExtra("releaseDate", track.releaseDate)
        audioPlayerActivity.putExtra("primaryGenreName", track.primaryGenreName)
        audioPlayerActivity.putExtra("country", track.country)
        audioPlayerActivity.putExtra("previewUrl", track.previewUrl)

        startActivity(audioPlayerActivity)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.SEARCH_TEXT_KEY, inputText)
    }
}