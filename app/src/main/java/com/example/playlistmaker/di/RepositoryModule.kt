package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.converter.FavouriteTrackDbConvertor
import com.example.playlistmaker.data.favourite.impl.FavouriteTrackRepositoryImpl
import com.example.playlistmaker.data.media_library.impl.MediaLibraryRepositoryImpl
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.player.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.search.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.db.FavouriteTrackRepository
import com.example.playlistmaker.domain.media_library.MediaLibraryRepository
import com.example.playlistmaker.domain.player.AudioPlayerRepository
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get(), get(),get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            storage = get(),
            maxHistorySize = 10
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<MediaPlayer> {
        MediaPlayer()
    }

    single<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    single<MediaLibraryRepository> {
        MediaLibraryRepositoryImpl()
    }

    factory { FavouriteTrackDbConvertor() }

    single<FavouriteTrackRepository> {
        FavouriteTrackRepositoryImpl(get(), get())
    }


}