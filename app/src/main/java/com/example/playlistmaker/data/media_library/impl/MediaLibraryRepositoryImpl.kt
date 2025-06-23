package com.example.playlistmaker.data.media_library.impl

import com.example.playlistmaker.R
import com.example.playlistmaker.domain.media_library.MediaLibraryRepository
import com.example.playlistmaker.domain.models.TabData
import com.example.playlistmaker.ui.favourite.fragment.FavouriteFragment
import com.example.playlistmaker.ui.playlist.fragment.PlaylistFragment

class MediaLibraryRepositoryImpl : MediaLibraryRepository {
    override fun getTabData() = listOf(
        TabData(R.string.favourites) { FavouriteFragment.newInstance() },
        TabData(R.string.playlists) { PlaylistFragment.newInstance() }
    )
}