package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val historyKey = "search_history"
    private val maxHistorySize = 10

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}