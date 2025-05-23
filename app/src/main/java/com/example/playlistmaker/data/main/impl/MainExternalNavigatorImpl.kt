package com.example.playlistmaker.data.main.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.ui.media_library.MediaLibraryActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainExternalNavigatorImpl  () : MainExternalNavigator {

    override fun openSearch(context: Context) {
        val searchIntent = Intent(context, SearchActivity::class.java)
        context.startActivity(searchIntent)
    }

    override fun openMediaLibrary(context: Context) {
        val mediaLibraryIntent = Intent(context, MediaLibraryActivity::class.java)
        context.startActivity(mediaLibraryIntent)
    }

    override fun openSettings(context: Context) {
        val settingsIntent = Intent(context, SettingsActivity::class.java)
        context.startActivity(settingsIntent)
    }
}