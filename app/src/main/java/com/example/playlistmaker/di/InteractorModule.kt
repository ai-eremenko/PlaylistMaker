package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.db.FavouriteTrackInteractor
import com.example.playlistmaker.domain.impl.FavouriteTrackInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.media_library.MediaLibraryInteractor
import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(
            repository = get(),
            historyInteractor = get()
        )
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(
            repository = get()
        )
    }

    single<AudioPlayerInteractor> {
        AudioPlayerInteractor(
            repository = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(
            settingsRepository = get()
        )
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get()
        )
    }

    single<MediaLibraryInteractor> {
        MediaLibraryInteractor(get())
    }

    single<FavouriteTrackInteractor> {
        FavouriteTrackInteractorImpl(get())
    }
}