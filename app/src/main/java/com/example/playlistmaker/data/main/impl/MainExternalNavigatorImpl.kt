package com.example.playlistmaker.data.main.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.ui.media_library.activity.MediaLibraryActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainExternalNavigatorImpl  (
    private val context: Context
) : MainExternalNavigator {

    override fun openSearch() {
        val searchIntent = Intent(context, SearchActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(searchIntent)
    }

    override fun openMediaLibrary() {
        val mediaLibraryIntent = Intent(context, MediaLibraryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(mediaLibraryIntent)
    }

    override fun openSettings() {
        val settingsIntent = Intent(context, SettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(settingsIntent)
    }
}