package com.example.playlistmaker.domain.search

import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    suspend fun getFavouriteIds(): List<String>
}