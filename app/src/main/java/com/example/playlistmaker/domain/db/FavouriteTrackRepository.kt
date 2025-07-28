package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTrackRepository {
    suspend fun addToFavourite(track: Track)
    suspend fun removeFromFavourite(track: Track)
    fun getFavourite(): Flow<List<Track>>
}