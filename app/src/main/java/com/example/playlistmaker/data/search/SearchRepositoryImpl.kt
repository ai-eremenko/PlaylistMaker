package com.example.playlistmaker.data.search

import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.app.TrackMapper
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
            try {
                val response = networkClient.doRequest(SearchRequest(expression))

                when(response.resultCode){
                    -1 -> {
                        emit(Resource.Error("Проверьте подключение к интернету"))
                    }
                    200 -> {
                        val tracksList = (response as SearchResponse).results.map { trackDto ->
                            trackMapper.map(trackDto)
                        }
                        emit(Resource.Success(tracksList))
                    }
                    else -> {
                        emit(Resource.Error("Ошибка сервера (код ${response.resultCode})"))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Неизвестная ошибка"))
            }
    }
}