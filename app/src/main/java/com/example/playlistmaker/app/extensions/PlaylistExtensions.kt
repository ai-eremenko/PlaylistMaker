package com.example.playlistmaker.app.extensions

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist

fun PlaylistEntity.toDomain(trackCount: Int): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        coverPath = this.coverPath,
        trackCount = trackCount,
        trackIds = this.trackIds
    )
}