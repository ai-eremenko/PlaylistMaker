package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.SearchRequest

class RetrofitNetworkClient(
    private val itunesService: ItunesApiService
) : NetworkClient {

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