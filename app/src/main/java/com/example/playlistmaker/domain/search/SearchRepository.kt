package com.example.playlistmaker.domain.search

import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}