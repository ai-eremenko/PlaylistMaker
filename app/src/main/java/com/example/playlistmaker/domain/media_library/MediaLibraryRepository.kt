package com.example.playlistmaker.domain.media_library

import com.example.playlistmaker.domain.models.TabData

interface MediaLibraryRepository {
    fun getTabData(): List<TabData>
}