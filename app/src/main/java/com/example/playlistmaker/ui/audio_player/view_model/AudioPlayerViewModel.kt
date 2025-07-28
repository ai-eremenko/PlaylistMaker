package com.example.playlistmaker.ui.audio_player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavouriteTrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.search.SearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favouriteTrackInteractor: FavouriteTrackInteractor,
    private val searchInteractor: SearchInteractor
) : ViewModel() {

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

    companion object {
        private const val TIMER_DEBOUNCE_DELAY = 300L
    }
}


