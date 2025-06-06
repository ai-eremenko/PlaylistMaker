package com.example.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: String): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

        fun newInstance() = PlaylistFragment()
    }

    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistsEmptyPlaceholder.visibility = View.VISIBLE
        binding.createPlaylistButton.visibility = View.VISIBLE
    }

}