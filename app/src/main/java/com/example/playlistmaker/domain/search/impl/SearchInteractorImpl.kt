package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.SearchRepository
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val historyInteractor: SearchHistoryInteractor
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }
                is Resource.Error -> {
                    consumer.consume(null, resource.message)
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