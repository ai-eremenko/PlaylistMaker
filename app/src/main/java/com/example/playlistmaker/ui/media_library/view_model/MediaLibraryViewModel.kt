package com.example.playlistmaker.ui.media_library.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.media_library.MediaLibraryInteractor
import com.example.playlistmaker.domain.models.TabData

class MediaLibraryViewModel(
    private val interactor: MediaLibraryInteractor
) : ViewModel() {

    fun getTabsData(): List<TabData> = interactor.getTabsData()
}