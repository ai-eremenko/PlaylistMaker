package com.example.playlistmaker.ui.audio_player.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.app.extensions.isGone
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import com.example.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AudioPlayerViewModel>()


    companion object {
        private const val ARG_TRACK_ID = "trackId"
        private const val ARG_TRACK_NAME = "trackName"
        private const val ARG_ARTIST_NAME = "artistName"
        private const val ARG_TRACK_TIME = "trackDuration"
        private const val ARG_ARTWORK_URL = "artworkUrl"
        private const val ARG_COLLECTION_NAME = "collectionName"
        private const val ARG_RELEASE_DATE = "releaseDate"
        private const val ARG_GENRE_NAME = "primaryGenreName"
        private const val ARG_COUNTRY = "country"
        private const val ARG_PREVIEW_URL = "previewUrl"

        fun createArgs(track: Track): Bundle {
            return bundleOf(
                ARG_TRACK_ID to track.trackId,
                ARG_TRACK_NAME to track.trackName,
                ARG_ARTIST_NAME to track.artistName,
                ARG_TRACK_TIME to track.trackTime,
                ARG_ARTWORK_URL to track.artworkUrl100,
                ARG_COLLECTION_NAME to track.collectionName,
                ARG_RELEASE_DATE to track.releaseDate,
                ARG_GENRE_NAME to track.primaryGenreName,
                ARG_COUNTRY to track.country,
                ARG_PREVIEW_URL to track.previewUrl
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("WrongViewCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = savedInstanceState ?: arguments

        val track = Track(
            trackId = bundle?.getString(ARG_TRACK_ID) ?: "Unknown Id",
            trackName = bundle?.getString(ARG_TRACK_NAME) ?: "Unknown Track",
            artistName = bundle?.getString(ARG_ARTIST_NAME) ?: "Unknown Artist",
            trackTime = bundle?.getString(ARG_TRACK_TIME) ?: "",
            artworkUrl100 = bundle?.getString(ARG_ARTWORK_URL) ?: "",
            collectionName = bundle?.getString(ARG_COLLECTION_NAME) ?: "Unknown Album",
            releaseDate = bundle?.getString(ARG_RELEASE_DATE) ?: "Unknown Year",
            primaryGenreName = bundle?.getString(ARG_GENRE_NAME) ?: "Unknown Genre",
            country = bundle?.getString(ARG_COUNTRY) ?: "Unknown Country",
            previewUrl = bundle?.getString(ARG_PREVIEW_URL) ?: ""
        )

        viewModel.setTrackData(track)
        setupViews()
        setupObservers()
        setupFavoriteButton()
    }

    private fun setupViews() {

        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.pauseButton.setOnClickListener {
            viewModel.pausePlayer()
        }
    }

    private fun setupObservers() {
        viewModel.trackData.observe(viewLifecycleOwner) { data ->
            updateUI(data)
        }

        viewModel.audioPlayerScreenState.observe(viewLifecycleOwner) { state ->
            updatePlayerState(state)
        }
    }

    private fun setupFavoriteButton() {
        binding.favoriteButtonConstraintLayout.setOnClickListener {
            viewModel.onFavouriteClicked()
        }

        viewModel.isFavourite.observe(viewLifecycleOwner) { isFavourite ->
            if (isFavourite==true) {
                binding.favoriteButton.visibility = View.GONE
                binding.pressedFavoriteButton.visibility = View.VISIBLE
            } else {
                binding.favoriteButton.visibility = View.VISIBLE
                binding.pressedFavoriteButton.visibility = View.GONE
            }
        }
    }

    private fun updateUI(data : Track) {
        binding.trackName.text = data.trackName
        binding.artistName.text = data.artistName
        binding.trackTime.text = data.trackTime
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
            binding.time.text = state.currentPosition
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
        outState.putString(ARG_TRACK_ID, data.trackId)
        outState.putString(ARG_TRACK_NAME, data.trackName)
        outState.putString(ARG_ARTIST_NAME, data.artistName)
        outState.putString(ARG_TRACK_TIME, data.trackTime)
        outState.putString(ARG_ARTWORK_URL, data.artworkUrl100)
        outState.putString(ARG_COLLECTION_NAME, data.collectionName)
        outState.putString(ARG_RELEASE_DATE, data.releaseDate)
        outState.putString(ARG_GENRE_NAME, data.primaryGenreName)
        outState.putString(ARG_COUNTRY, data.country)
        outState.putString(ARG_PREVIEW_URL, data.previewUrl)
    }
}

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
