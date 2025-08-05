package com.example.playlistmaker.ui.audio_player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.app.extensions.toDomain
import com.example.playlistmaker.domain.db.favourite_track.FavouriteTrackInteractor
import com.example.playlistmaker.domain.db.playlist.AddTrackResult
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.ui.audio_player.AddTrackStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favouriteTrackInteractor: FavouriteTrackInteractor,
    private val searchInteractor: SearchInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _addTrackStatus = MutableLiveData<AddTrackStatus>()
    val addTrackStatus: LiveData<AddTrackStatus> = _addTrackStatus

    private val _audioPlayerScreenState =
        MutableLiveData<AudioPlayerScreenState>(AudioPlayerScreenState.Default)

    val audioPlayerScreenState: LiveData<AudioPlayerScreenState> = _audioPlayerScreenState

    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private var currentPosition = 0

    private var timerJob: Job? = null

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private var currentTrack: Track? = null

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists


    init {
        audioPlayerInteractor.setOnPreparedListener {
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Prepared)
        }
        audioPlayerInteractor.setOnCompletionListener {
            stopUpdatingTime()
            audioPlayerInteractor.seekTo(0)
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Prepared)
        }


    }

    fun setTrackData(data: Track) {
        _trackData.value = data
        audioPlayerInteractor.preparePlayer(data.previewUrl)

        viewModelScope.launch {
            val favoriteIds = searchInteractor.getFavouriteIds()
            _isFavourite.postValue(favoriteIds.contains(data.trackId))
        }
    }

    fun playbackControl() {
        when (_audioPlayerScreenState.value) {
            is AudioPlayerScreenState.Playing -> pausePlayer()
            is AudioPlayerScreenState.Prepared, is AudioPlayerScreenState.Paused -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        if (audioPlayerInteractor.isPrepared()) {
            if (audioPlayerInteractor.getCurrentPosition() >= audioPlayerInteractor.getDuration()) {
                audioPlayerInteractor.seekTo(0)
            }
            audioPlayerInteractor.startPlayer()
            startUpdatingTime()
        }
    }

    fun pausePlayer() {
        if (audioPlayerInteractor.isPlaying()) {
            currentPosition = audioPlayerInteractor.getCurrentPosition()
            audioPlayerInteractor.pausePlayer()
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Paused(formatTime(currentPosition)))
            stopUpdatingTime()
        }
    }

    private fun startUpdatingTime() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var currentPosition = audioPlayerInteractor.getCurrentPosition()
            _audioPlayerScreenState.postValue(
                AudioPlayerScreenState.Playing(formatTime(currentPosition))
            )

            while(audioPlayerInteractor.isPlaying()) {
                delay(TIMER_DEBOUNCE_DELAY)
                currentPosition = audioPlayerInteractor.getCurrentPosition()
                _audioPlayerScreenState.postValue(
                    AudioPlayerScreenState.Playing(formatTime(currentPosition))
                )
            }
        }
    }


    private fun stopUpdatingTime() {
        timerJob?.cancel()
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(millis.toLong()))
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerInteractor.releasePlayer()
        stopUpdatingTime()
    }

    fun onFavouriteClicked() {
        _trackData.value?.let { track ->
            viewModelScope.launch {
                val currentIsFavourite = _isFavourite.value ?: false
                if (currentIsFavourite) {
                    favouriteTrackInteractor.removeFromFavourite(track)
                } else {
                    favouriteTrackInteractor.toggleFavourite(track)
                }
                _isFavourite.postValue(!currentIsFavourite)
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                val playlists = playlistInteractor.getAllPlaylists().map {
                    it.toDomain(playlistInteractor.getPlaylistTrackCount(it))
                }
                _playlists.postValue(playlists)
            } catch (e: Exception) {
                _addTrackStatus.postValue(AddTrackStatus.Error(R.string.no_playlist.toString()))
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        _trackData.value?.let { track ->
            if (playlist.trackIds.contains(track.trackId)) {
                _addTrackStatus.value = AddTrackStatus.AlreadyExists
                return
            }

            viewModelScope.launch {
                when (val result = playlistInteractor.addTrackToPlaylist(playlist, track)) {
                    AddTrackResult.Success -> _addTrackStatus.value = AddTrackStatus.Success
                    AddTrackResult.AlreadyExists -> _addTrackStatus.value = AddTrackStatus.AlreadyExists
                    is AddTrackResult.Error -> _addTrackStatus.value = AddTrackStatus.Error(result.message)
                }
            }
        }
    }

    companion object {
        private const val TIMER_DEBOUNCE_DELAY = 300L
    }
}


