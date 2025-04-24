package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (dto !is SearchRequest) {
                return Response().apply { resultCode = 400 }
            }

            val resp = itunesService.search(dto.expression).execute()

            if (!resp.isSuccessful) {
                return Response().apply { resultCode = resp.code() }
            }

            return resp.body()?.apply { resultCode = resp.code() } ?: Response().apply {
                resultCode = 404
            }
        } catch (e: Exception) {
            return Response().apply { resultCode = -1 }
        }
    }
}