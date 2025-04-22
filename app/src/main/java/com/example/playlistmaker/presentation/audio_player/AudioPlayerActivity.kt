package com.example.playlistmaker.presentation.audio_player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.extensions.isGone
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var prepared = false

    private lateinit var time: TextView
    private var mainThreadHandler: Handler? = null
    private var updateRunnable: Runnable? = null

    private lateinit var backButton: ImageView
    private lateinit var trackName: String
    private lateinit var artistName: String
    private var trackDuration: Long = 0
    private lateinit var artworkUrl: String
    private lateinit var collectionName: String
    private lateinit var releaseDate: String
    private lateinit var primaryGenreName: String
    private lateinit var country: String
    private lateinit var previewUrl: String

    private lateinit var playButton: FrameLayout
    private lateinit var pauseButton: FrameLayout

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backButton = findViewById(R.id.backButtonPlayer)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)

        playButton.isGone = false
        pauseButton.isGone = true

        time = findViewById(R.id.time)
        mainThreadHandler = Handler(Looper.getMainLooper())

        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                prepared = true
                playButton.isEnabled = true
                playerState = STATE_PREPARED

            }
            setOnCompletionListener {
                playerState = STATE_PREPARED
                stopUpdatingTime()
                time.text = "00:00"
                updateButtonVisibility()
            }
        }

        trackName = intent.getStringExtra("trackName") ?: "Unknown Track"
        artistName = intent.getStringExtra("artistName") ?: "Unknown Artist"
        trackDuration = intent.getLongExtra("trackDuration", 0)
        artworkUrl = intent.getStringExtra("artworkUrl") ?: ""
        collectionName = intent.getStringExtra("collectionName") ?: "Unknown Album"
        releaseDate = intent.getStringExtra("releaseDate") ?: "Unknown Year"
        primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "Unknown Genre"
        country = intent.getStringExtra("country") ?: "Unknown Country"
        this.previewUrl = intent.getStringExtra("previewUrl") ?: ""

        updateUI()
        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

        pauseButton.setOnClickListener {
           pausePlayer()
        }

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
        outState.putString("previewUrl", previewUrl)
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
        previewUrl = savedInstanceState.getString("previewUrl") ?: ""
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        stopUpdatingTime()
        updateRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            stopUpdatingTime()
            time.text = "00:00"
            updateButtonVisibility()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        updateButtonVisibility()
        createUpdateTimeRunnable()
        updateRunnable?.let { mainThreadHandler?.post(it) }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        updateButtonVisibility()
        stopUpdatingTime()
    }

    private fun updateButtonVisibility() {
        if (playerState == STATE_PLAYING) {
            playButton.isGone = true
            pauseButton.isGone = false
        } else {
            playButton.isGone = false
            pauseButton.isGone = true
        }
    }

    private fun createUpdateTimeRunnable() {
        updateRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))
                    time.text = formattedTime
                    mainThreadHandler?.postDelayed(this, 300)
                }
            }
        }
    }
    private fun stopUpdatingTime() {
        updateRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
    }
}