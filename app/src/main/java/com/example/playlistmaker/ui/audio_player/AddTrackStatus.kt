package com.example.playlistmaker.ui.audio_player

sealed class AddTrackStatus {
    object Success : AddTrackStatus()
    object AlreadyExists : AddTrackStatus()
    class Error(val message: String) : AddTrackStatus()
}