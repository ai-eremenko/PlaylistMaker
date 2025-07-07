package com.example.playlistmaker.data.network;

import com.example.playlistmaker.data.dto.SearchResponse
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ItunesApiService {
    @GET("search")
    suspend fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): SearchResponse
}


