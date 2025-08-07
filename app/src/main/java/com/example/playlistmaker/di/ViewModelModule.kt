package com.example.playlistmaker.di

import com.example.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.favourite.view_model.FavouriteViewModel
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.media_library.view_model.MediaLibraryViewModel
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        AudioPlayerViewModel(
            audioPlayerInteractor = get(),
            favouriteTrackInteractor = get(),
            searchInteractor = get(),
            playlistInteractor = get()
        )
    }


    viewModel {
        SearchViewModel(
            application = androidApplication(),
            interactor = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }

    viewModel {
        MediaLibraryViewModel(get())
    }

    viewModel { FavouriteViewModel(get()) }

    viewModel { PlaylistViewModel(get()) }

    viewModel { NewPlaylistViewModel(get()) }

}