package com.example.playlistmaker.data.search

import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.app.TrackMapper
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchRepository

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val historyInteractor: SearchHistoryInteractor,
    private val trackMapper: TrackMapper
) : SearchRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        return try {
            println("DEBDEB SAGUR repository searching in SearchRepositoryImpl")
            val response = networkClient.doRequest(SearchRequest(expression))
            val historyIds = historyInteractor.getHistory().map { it.trackId }
            println("DEBDEB SAGUR repository searching in SearchRepositoryImpl ${response.resultCode}")
            val tracksList = (response as? SearchResponse)?.results?.map { trackDto ->
                Track(
                    trackId = trackDto.trackId.toString(),
                    trackName = trackDto.trackName,
                    artistName = trackDto.artistName,
                    trackTimeMillis = trackDto.trackTimeMillis.toString(),
                    artworkUrl100 = trackDto.artworkUrl100,
                    collectionName = trackDto.collectionName,
                    releaseDate = trackDto.releaseDate,
                    primaryGenreName = trackDto.primaryGenreName,
                    country = trackDto.country,
                    previewUrl = trackDto.previewUrl
                )
            }.orEmpty()
            println("DEBDEB SAGUR repository found tracks in SearchRepositoryImpl")
            Resource.Success(tracksList)

        } catch (e: Exception) {
            println("DEBDEB SAGUR repository found exception in SearchRepositoryImpl")
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    override fun clearSearchHistory() {
        historyInteractor.clearHistory()
    }

    override fun getSearchHistory(): List<Track> {
        return historyInteractor.getHistory()
    }
}