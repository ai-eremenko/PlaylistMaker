package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage,
    private val maxHistorySize: Int = 10
) : SearchHistoryRepository {

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > maxHistorySize) {
            history.removeLast()
        }

        storage.saveHistory(history)
    }

    override fun getHistory(): List<Track> = storage.getHistory()

    override fun clearHistory() = storage.clearHistory()
}