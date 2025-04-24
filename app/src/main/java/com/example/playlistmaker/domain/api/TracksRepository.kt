package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import java.io.IOException

interface TracksRepository {
    @Throws(IOException::class)
    fun searchTracks(expression: String): List<Track>
}