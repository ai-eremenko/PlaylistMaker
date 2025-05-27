package com.example.playlistmaker.ui.audio_player.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.app.extensions.isGone
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import com.example.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val viewModel by viewModel<AudioPlayerViewModel>()

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = Track(
            trackId =  intent.getStringExtra("trackId") ?: "Unknown Id",
            trackName = intent.getStringExtra("trackName") ?: "Unknown Track",
            artistName = intent.getStringExtra("artistName") ?: "Unknown Artist",
            trackTime = intent.getStringExtra("trackDuration") ?: "",
            artworkUrl100 = intent.getStringExtra("artworkUrl") ?: "",
            collectionName = intent.getStringExtra("collectionName") ?: "Unknown Album",
            releaseDate = intent.getStringExtra("releaseDate") ?: "Unknown Year",
            primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "Unknown Genre",
            country = intent.getStringExtra("country") ?: "Unknown Country",
            previewUrl = intent.getStringExtra("previewUrl") ?: ""
        )

        viewModel.setTrackData(track)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {

        binding.backButtonPlayer.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.pauseButton.setOnClickListener {
            viewModel.pausePlayer()
        }
    }

    private fun setupObservers() {
        viewModel.trackData.observe(this) { data ->
            updateUI(data)
        }

        viewModel.audioPlayerScreenState.observe(this) { state ->
            updatePlayerState(state)
        }
    }

    private fun updateUI(data : Track) {
        binding.trackName.text = data.trackName
        binding.artistName.text = data.artistName
        binding.duration.text = data.trackTime
        binding.collectionName.text = data.collectionName
        binding.releaseDate.text = data.getReleaseYear()
        binding.primaryGenreName.text =  data.primaryGenreName
        binding.country.text  = data.country

        Glide.with(this)
            .load(data.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.sourceImage)
    }


private fun updatePlayerState(state: AudioPlayerScreenState) {
    when (state) {
        is AudioPlayerScreenState.Playing -> {
            binding.playButton.isGone = true
            binding.pauseButton.isGone = false
            binding.time.text = state.currentPosition
        }
        is  AudioPlayerScreenState.Paused -> {
            binding.playButton.isGone = false
            binding.pauseButton.isGone = true
        }
        is  AudioPlayerScreenState.Prepared -> {
            binding.playButton.isGone = false
            binding.pauseButton.isGone = true
            binding.time.text = "00:00"
        }
        else -> {}
    }
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel.trackData.value?.let { data ->
        outState.putString("trackId", data.trackId)
        outState.putString("trackName", data.trackName)
        outState.putString("artistName", data.artistName)
        outState.putString("trackDuration", data.trackTime)
        outState.putString("artworkUrl", data.artworkUrl100)
        outState.putString("collectionName", data.collectionName)
        outState.putString("releaseDate", data.releaseDate)
        outState.putString("primaryGenreName", data.primaryGenreName)
        outState.putString("country", data.country)
        outState.putString("previewUrl", data.previewUrl)
    }
}

override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    val track = Track(
        trackId = savedInstanceState.getString("trackId") ?: "Unknown Id",
        trackName = savedInstanceState.getString("trackName") ?: "Unknown Track",
        artistName = savedInstanceState.getString("artistName") ?: "Unknown Artist",
        trackTime = savedInstanceState.getString("trackDuration") ?: "",
        artworkUrl100 = savedInstanceState.getString("artworkUrl") ?: "",
        collectionName = savedInstanceState.getString("collectionName") ?: "Unknown Album",
        releaseDate = savedInstanceState.getString("releaseDate") ?: "Unknown Year",
        primaryGenreName = savedInstanceState.getString("primaryGenreName") ?: "Unknown Genre",
        country = savedInstanceState.getString("country") ?: "Unknown Country",
        previewUrl = savedInstanceState.getString("previewUrl") ?: ""
    )
    viewModel.setTrackData(track)
}

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
}
