package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val historyInteractor: SearchHistoryInteractor
) : SearchInteractor {

    override fun searchTracks(expression: String) : Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair( result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    override fun clearSearchHistory() {
        historyInteractor.clearHistory()
    }

    override fun getSearchHistory(): List<Track> {
        return  historyInteractor.getHistory()
    }
}