package com.example.playlistmaker.app

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track


class TrackMapper {
    fun toTrack(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId.toString(),
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = getFormattedDuration(dto.trackTimeMillis),
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    fun getFormattedDuration(millis: Long): String {
        val dateFormat = java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
        return dateFormat.format(millis)
    }
}