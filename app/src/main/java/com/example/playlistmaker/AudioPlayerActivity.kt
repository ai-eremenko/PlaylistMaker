package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var trackName: String
    private lateinit var artistName: String
    private var trackDuration: Long = 0
    private lateinit var artworkUrl: String
    private lateinit var collectionName: String
    private lateinit var releaseDate: String
    private lateinit var primaryGenreName: String
    private lateinit var country: String

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backButton = findViewById(R.id.backButtonPlayer)

        trackName = intent.getStringExtra("trackName") ?: "Unknown Track"
        artistName = intent.getStringExtra("artistName") ?: "Unknown Artist"
        trackDuration = intent.getLongExtra("trackDuration", 0)
        artworkUrl = intent.getStringExtra("artworkUrl") ?: ""
        collectionName = intent.getStringExtra("collectionName") ?: "Unknown Album"
        releaseDate = intent.getStringExtra("releaseDate") ?: "Unknown Year"
        primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "Unknown Genre"
        country = intent.getStringExtra("country") ?: "Unknown Country"

        updateUI()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun updateUI() {
        val trackNameTextView: TextView = findViewById(R.id.trackName)
        val artistNameTextView: TextView = findViewById(R.id.artistName)
        val durationTextView: TextView = findViewById(R.id.trackTime)
        val collectionNameTextView: TextView = findViewById(R.id.collectionName)
        val releaseDateTextView: TextView = findViewById(R.id.releaseDate)
        val primaryGenreNameTextView: TextView = findViewById(R.id.primaryGenreName)
        val countryTextView: TextView = findViewById(R.id.country)

        trackNameTextView.text = trackName
        artistNameTextView.text = artistName
        durationTextView.text = formatDuration(trackDuration)
        collectionNameTextView.text = collectionName
        releaseDateTextView.text = OffsetDateTime.parse(releaseDate).year.toString()
        primaryGenreNameTextView.text = primaryGenreName
        countryTextView.text = country

        val sourceImageView: ImageView = findViewById(R.id.sourceImage)
        Glide.with(this)
            .load(getCoverArtwork(artworkUrl))
            .placeholder(R.drawable.placeholder2)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(sourceImageView)
    }

    private fun getCoverArtwork(artworkUrl: String): String {
        return artworkUrl.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun formatDuration(durationMillis: Long): String {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return dateFormat.format(durationMillis)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("trackName", trackName)
        outState.putString("artistName", artistName)
        outState.putLong("trackDuration", trackDuration)
        outState.putString("artworkUrl", artworkUrl)
        outState.putString("collectionName", collectionName)
        outState.putString("releaseDate", releaseDate)
        outState.putString("primaryGenreName", primaryGenreName)
        outState.putString("country", country)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackName = savedInstanceState.getString("trackName") ?: "Unknown Track"
        artistName = savedInstanceState.getString("artistName") ?: "Unknown Artist"
        trackDuration = savedInstanceState.getLong("trackDuration", 0)
        artworkUrl = savedInstanceState.getString("artworkUrl") ?: ""
        collectionName = savedInstanceState.getString("collectionName") ?: "Unknown Album"
        releaseDate = savedInstanceState.getString("releaseDate") ?: "Unknown Year"
        primaryGenreName = savedInstanceState.getString("primaryGenreName") ?: "Unknown Genre"
        country = savedInstanceState.getString("country") ?: "Unknown Country"
        updateUI()
    }
}