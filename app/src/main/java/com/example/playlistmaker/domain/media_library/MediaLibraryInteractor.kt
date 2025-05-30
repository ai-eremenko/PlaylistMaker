package com.example.playlistmaker.domain.media_library

import com.example.playlistmaker.domain.models.TabData

class MediaLibraryInteractor(private val repository: MediaLibraryRepository) {
    fun getTabsData(): List<TabData> = repository.getTabData()
}