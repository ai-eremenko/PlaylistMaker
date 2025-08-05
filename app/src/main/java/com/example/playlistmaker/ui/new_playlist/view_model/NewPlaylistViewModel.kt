package com.example.playlistmaker.ui.new_playlist.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.playlist.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    var hasUnsavedChanges = false
    var coverPath: String? = null
    var playlistName: String = ""
    var playlistDescription: String = ""

    fun savePlaylist(
        name: String,
        description: String,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isEmpty()) {
            onError("playlist_name_required")
            return
        }

        viewModelScope.launch {
            try {
                val playlistId  = playlistInteractor.createPlaylist(
                    name = name,
                    description = description,
                    coverPath = coverPath
                )
                onSuccess(playlistId)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("playlist_save_error")
            }
        }
    }

    fun saveImageToPrivateStorage(uri: Uri, picturesDir: File, requireContext: Context): String? {
        val filePath = File(picturesDir, "playlist_covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "playlist_cover_${System.currentTimeMillis()}.jpg")

        return try {
            val inputStream = requireContext.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory.decodeStream(inputStream)?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}