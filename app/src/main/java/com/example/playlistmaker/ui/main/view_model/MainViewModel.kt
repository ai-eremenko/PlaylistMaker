package com.example.playlistmaker.ui.main.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.main.MainInteractor

class MainViewModel (
    private val mainInteractor: MainInteractor,
) : ViewModel () {
    fun searchButton(context: Context) {
        mainInteractor.searchButton(context)
    }

    fun mediaLibraryButton(context: Context) {
        mainInteractor.mediaLibraryButton(context)
    }

    fun settingsButton(context: Context) {
        mainInteractor.settingsButton(context)
    }
}