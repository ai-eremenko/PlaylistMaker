package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorageImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryStorage {

    private val historyKey = "search_history"

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    override fun saveHistory(history: List<Track>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}