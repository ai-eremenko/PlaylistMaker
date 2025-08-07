package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.db.playlist.AddTrackResult
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.playlistmaker.domain.db.playlist.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
    private val gson: Gson
) : PlaylistInteractor {

    override suspend fun createPlaylist(name: String, description: String, coverPath: String?) =
        repository.createPlaylist(name, description, coverPath)

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylist(id: Long) = repository.getPlaylist(id)

    override suspend fun getPlaylistTrackCount(playlist: PlaylistEntity): Int =
        repository.getPlaylistTracksCount(playlist)

    override suspend fun getAllPlaylists() = repository.getAllPlaylists()

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult {
        val playlistEntity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = playlist.trackIds
        )

        return repository.addTrackToPlaylist(playlistEntity, track)
    }
}
