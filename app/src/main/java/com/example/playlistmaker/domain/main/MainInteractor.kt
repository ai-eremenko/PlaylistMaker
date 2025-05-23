package com.example.playlistmaker.domain.main

import android.content.Context

interface MainInteractor {
    fun searchButton(context: Context)
    fun mediaLibraryButton(context: Context)
    fun settingsButton(context: Context)
}