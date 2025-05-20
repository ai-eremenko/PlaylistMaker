package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.local.SearchHistoryStorageImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.app.TrackMapper
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.main.impl.MainExternalNavigatorImpl
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.player.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.search.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.domain.main.MainInteractor
import com.example.playlistmaker.domain.main.impl.MainInteractorImpl
import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.player.AudioPlayerRepository
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.google.gson.Gson

object Creator {

    fun getTrackMapper(): TrackMapper = TrackMapper()

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            repository = getSearchRepository(),
            historyInteractor = provideSearchHistoryInteractor(context)
        )
    }

    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(
            networkClient = RetrofitNetworkClient(),
            trackMapper = getTrackMapper()
        )
    }

    private fun getSearchHistoryStorage(context: Context): SearchHistoryStorage {
        return SearchHistoryStorageImpl(
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE),
            Gson()
        )
    }

    fun provideMainExternalNavigator(context: Context): MainExternalNavigator {
        return MainExternalNavigatorImpl(context)
    }

    fun provideMainInteractor(externalNavigator: MainExternalNavigator): MainInteractor {
        return MainInteractorImpl(externalNavigator)
    }


    private fun provideAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractor(provideAudioPlayerRepository())
    }

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            provideSettingsRepository(context)
        )
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = provideExternalNavigator(context)
        )
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            getSearchHistoryStorage(context),
            maxHistorySize = 10
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            provideSearchHistoryRepository(context)
        )
    }
}