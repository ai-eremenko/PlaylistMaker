package com.example.playlistmaker.ui.playlist_info.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.converter.toTrack
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.db.playlist.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val gson: Gson,
    private val repository: PlaylistRepository
) : ViewModel() {
    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String> = _duration

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlistEntity = playlistInteractor.getPlaylist(playlistId)
            playlistEntity?.let {
                val trackCount = playlistInteractor.getPlaylistTrackCount(it)
                _playlist.postValue(
                    Playlist(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        coverPath = it.coverPath,
                        trackCount = trackCount,
                        trackIds = it.trackIds
                    )
                )
                val trackIds = gson.fromJson<List<String>>(it.trackIds, object : TypeToken<List<String>>() {}.type)
                    ?: emptyList()
                val tracks = playlistInteractor.getTracksByPlaylist(trackIds).map { it.toTrack() }
                _tracks.postValue(tracks)
            }
        }
    }

    fun calculateDuration(trackIdsJson: String) {
        viewModelScope.launch {
            try {
                val trackIds = gson.fromJson<List<String>>(
                    trackIdsJson,
                    object : TypeToken<List<String>>() {}.type
                ) ?: emptyList()

                _duration.postValue(
                    playlistInteractor.calculateTotalDuration(trackIds)
                )
            } catch (e: Exception) {
                _duration.postValue("00:00")
            }
        }
    }

    fun deleteTrack(playlistId: Long, trackId: String) {
        viewModelScope.launch {
            repository.deleteTrackFromPlaylist(playlistId, trackId)
            loadPlaylist(playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }
}