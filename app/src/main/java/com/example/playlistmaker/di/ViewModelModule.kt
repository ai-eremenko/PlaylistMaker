package com.example.playlistmaker.di

import com.example.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.media_library.fragments.favourite_fragment.FavouriteViewModel
import com.example.playlistmaker.ui.media_library.fragments.playlist_fragment.PlaylistViewModel
import com.example.playlistmaker.ui.media_library.view_model.MediaLibraryViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        AudioPlayerViewModel(
            audioPlayerInteractor = get()
        )
    }

    viewModel {
        MainViewModel(
            mainInteractor = get()
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

    viewModel { FavouriteViewModel() }

    viewModel { PlaylistViewModel() }

}