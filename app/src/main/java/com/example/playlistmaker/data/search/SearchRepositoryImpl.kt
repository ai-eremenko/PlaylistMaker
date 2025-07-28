package com.example.playlistmaker.data.search

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.app.Resource
import com.example.playlistmaker.app.TrackMapper
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper,
    private val context: Context,
    private val appDatabase: AppDatabase
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
            try {
                val response = networkClient.doRequest(SearchRequest(expression))

                when(response.resultCode){
                    -1 -> {
                        emit(Resource.Error(context.getString(R.string.error_network_connection)))
                    }
                    200 -> {
                        val tracksList = (response as SearchResponse).results.map { trackDto ->
                            trackMapper.map(trackDto)
                        }
                        emit(Resource.Success(tracksList))
                    }
                    else -> {
                        val errorMsg = context.getString(R.string.error_server, response.resultCode)
                        emit(Resource.Error(errorMsg))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(context.getString(R.string.error_unknown)))
            }
    }

    override suspend fun getFavouriteIds(): List<String> {
        return appDatabase.favouriteTrackDao().getFavouriteId().first()
    }
}