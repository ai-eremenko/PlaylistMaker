package com.example.playlistmaker.ui.favourite.state

import com.example.playlistmaker.domain.models.Track

sealed class FavouriteTrackState {
    object Empty : FavouriteTrackState()
    data class Content(val tracks: List<Track>) : FavouriteTrackState()
}