package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTrackInteractor {
    suspend fun toggleFavourite(track: Track)
    fun getFavourite(): Flow<List<Track>>
    suspend fun removeFromFavourite(track: Track)
}