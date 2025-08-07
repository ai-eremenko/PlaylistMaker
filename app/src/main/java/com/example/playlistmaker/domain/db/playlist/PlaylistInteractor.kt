package com.example.playlistmaker.domain.db.playlist

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylist(id: Long): PlaylistEntity?
    suspend fun getPlaylistTrackCount(playlist: PlaylistEntity): Int
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult
}