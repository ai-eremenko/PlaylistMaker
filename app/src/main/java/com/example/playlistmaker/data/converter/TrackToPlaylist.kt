package com.example.playlistmaker.data.converter

import androidx.room.PrimaryKey
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.models.Track

fun Track.toPlaylistTrackEntity() = PlaylistTrackEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isFavourite = isFavourite
)