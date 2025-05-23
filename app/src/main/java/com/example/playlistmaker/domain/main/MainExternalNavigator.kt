package com.example.playlistmaker.domain.main

import android.content.Context

interface MainExternalNavigator {
    fun openSearch(context: Context)
    fun openMediaLibrary(context: Context)
    fun openSettings(context: Context)
}