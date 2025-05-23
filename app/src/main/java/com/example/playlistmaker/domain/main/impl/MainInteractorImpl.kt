package com.example.playlistmaker.domain.main.impl

import android.content.Context
import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.domain.main.MainInteractor

class MainInteractorImpl(
    private val mainExternalNavigator: MainExternalNavigator,
) : MainInteractor {

    override fun searchButton(context: Context) {
        mainExternalNavigator.openSearch(context)
    }

    override fun mediaLibraryButton(context: Context) {
        mainExternalNavigator.openMediaLibrary(context)
    }

    override fun settingsButton(context: Context) {
        mainExternalNavigator.openSettings(context)
    }
}