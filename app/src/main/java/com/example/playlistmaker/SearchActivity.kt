package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private var inputText: String? = null
    private lateinit var backButton: ImageView
    private lateinit var clearIcon: ImageView

    private lateinit var trackList: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var updateButton: Button
    private var lastSearchQuery: String? = null

    private lateinit var noResultsLayout : ConstraintLayout
    private lateinit var noInternetLayout : ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        clearIcon = findViewById(R.id.clearIcon)
        inputEditText = findViewById(R.id.inputEditText)

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                clearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearIcon.setOnClickListener {
            inputEditText.text.clear()
            inputEditText.clearFocus()
            trackList.visibility = RecyclerView.GONE
            clearIcon.visibility = View.INVISIBLE

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        inputEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)

        savedInstanceState?.let {
            inputText = it.getString(Constants.SEARCH_TEXT_KEY)
            inputEditText.setText(inputText)
        }


        trackList = findViewById(R.id.trackList)
        updateButton = findViewById(R.id.update_button)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        noResultsLayout= findViewById(R.id.noResultsLayout)

        trackList.isGone = true
        noInternetLayout.isGone = true
        noResultsLayout.isGone = true

        trackAdapter = TrackAdapter(emptyList())
        trackList.layoutManager = LinearLayoutManager(this)
        trackList.adapter = trackAdapter


        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =  getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                inputEditText.clearFocus()
                performSearch(inputEditText.text.toString())
                true
            } else {
                false
            }
        }
        updateButton.setOnClickListener {
            lastSearchQuery?.let { query -> performSearch(query) }
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
        val itunesApiService = retrofit.create<ItunesApiService>()
        itunesApiService.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.resultCount > 0) {
                        trackAdapter.setTracks(searchResponse.results)
                        trackList.isGone = false
                        noInternetLayout.isGone = true
                        noResultsLayout.isGone = true
                    } else {
                        trackList.isGone = true
                        noResultsLayout.isGone = false
                        noInternetLayout.isGone = true
                    }
                } else {
                    trackList.isGone = true
                    noResultsLayout.isGone = true
                    noInternetLayout.isGone = false
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                trackList.isGone = true
                noResultsLayout.isGone = true
                noInternetLayout.isGone = false
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
}
