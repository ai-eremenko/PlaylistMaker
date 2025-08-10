package com.example.playlistmaker.data.player.impl

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.app.extensions.isGone
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.AudioPlayerRepository
import com.example.playlistmaker.ui.audio_player.AddTrackStatus
import com.example.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import com.example.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.bottom_sheet.PlaylistBottomSheetAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {

    private var prepared = false
    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun preparePlayer(url: String) {
        if (url.isBlank()) {
            onPreparedListener?.invoke()
            return
        }

        releasePlayer()
        try {
            mediaPlayer.apply {
                setDataSource(url)
                setOnPreparedListener {
                    prepared = true
                    onPreparedListener?.invoke()
                }
                setOnCompletionListener {
                    onCompletionListener?.invoke()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            releasePlayer()
            onPreparedListener?.invoke()
        }
    }


    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.apply{
            reset()
        }
        prepared = false
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun isPrepared(): Boolean = prepared

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }
}