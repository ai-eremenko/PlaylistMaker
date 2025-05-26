package com.example.playlistmaker.domain.main.impl

import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.domain.main.MainInteractor

class MainInteractorImpl(
    private val mainExternalNavigator: MainExternalNavigator,
) : MainInteractor {

    override fun searchButton() {
        mainExternalNavigator.openSearch()
    }

    override fun mediaLibraryButton() {
        mainExternalNavigator.openMediaLibrary()
    }

    override fun settingsButton() {
        mainExternalNavigator.openSettings()
    }
}