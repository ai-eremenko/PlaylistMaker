package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.favourite_track.FavouriteTrackInteractor
import com.example.playlistmaker.domain.db.favourite_track.FavouriteTrackRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavouriteTrackInteractorImpl (
    private val repository: FavouriteTrackRepository
) : FavouriteTrackInteractor {

    override suspend fun toggleFavourite(track: Track) {
        if (track.isFavourite) {
            repository.removeFromFavourite(track)
        } else {
            repository.addToFavourite(track)
        }
    }

    override fun getFavourite(): Flow<List<Track>> {
        return repository.getFavourite()
    }

    override suspend  fun removeFromFavourite(track: Track) {
        repository.removeFromFavourite(track)
    }


}