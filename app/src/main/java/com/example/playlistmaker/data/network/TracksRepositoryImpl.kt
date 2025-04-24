package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackMapper
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))

        if (response.resultCode != 200) {
            throw IOException("Server returned error code: ${response.resultCode}")
        }

        val searchResponse = response as? SearchResponse
            ?: throw IOException("Invalid response type: ${response.javaClass.simpleName}")

        return searchResponse.results.map { dto ->
            trackMapper.toTrack(dto)
        }
    }
}
