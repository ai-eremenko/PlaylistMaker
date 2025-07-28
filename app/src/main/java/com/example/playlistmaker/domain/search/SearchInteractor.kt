package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?,String?>>
    fun addTrackToHistory(track: Track)
    suspend fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
    suspend fun getFavouriteIds(): List<String>
}