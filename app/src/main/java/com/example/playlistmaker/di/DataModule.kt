package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.app.Constants
import com.example.playlistmaker.app.TrackMapper
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.local.SearchHistoryStorageImpl
import com.example.playlistmaker.data.main.impl.MainExternalNavigatorImpl
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single {
        TrackMapper()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SearchHistoryStorage> {
        SearchHistoryStorageImpl(get(), get())
    }


    single<MainExternalNavigator> {
        MainExternalNavigatorImpl(androidContext())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            storage = get(),
            maxHistorySize = 10
        )
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }
}
