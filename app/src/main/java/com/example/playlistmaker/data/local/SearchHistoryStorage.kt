package com.example.playlistmaker.data.local

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryStorage {
    fun saveHistory(history: List<Track>)
    fun getHistory(): List<Track>
    fun clearHistory()
}
