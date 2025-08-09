package com.example.playlistmaker.ui.playlist_info.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.playlist_info.adapter.TrackInPlaylistAdapter
import com.example.playlistmaker.ui.playlist_info.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistInfoViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: TrackInPlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
        viewModel.loadPlaylist(playlistId)

        setupObservers()
        setupClickListeners()
        setupAdapter()
        setupMenuBottomSheet()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsInfoBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.playlistsRecyclerView.adapter = adapter
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun setupAdapter() {
        adapter = TrackInPlaylistAdapter(
            emptyList(),
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistInfoFragment_to_audioPlayerFragment,
                    bundleOf("track" to track)
                )
            },
            onTrackLongClick = { track ->
                showDeleteDialog(track)
            }
        )
    }

    private fun showDeleteDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.want_to_delete))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                val playlistId = arguments?.getLong("playlist_id") ?: 0L
                viewModel.deleteTrack(playlistId, track.trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun setupObservers() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.playlistName.text = playlist.name
            binding.yearOfPlaylist.text = playlist.description
            binding.trackCount.text = playlist.trackCount.toString()

            playlist.coverPath?.let { path ->
                Glide.with(this)
                    .load(path)
                    .into(binding.placeholderNewPlaylist)
            }

            viewModel.calculateDuration(playlist.trackIds)
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
        }

        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.timeDuration.text = duration
        }
    }

    private fun setupClickListeners() {
        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ShareButton.setOnClickListener {
            viewModel.tracks.value?.let { tracks ->
                if (tracks.isEmpty()) {
                    showToast(getString(R.string.no_tracks_to_share))
                } else {
                    val playlist = viewModel.playlist.value
                    val shareText = buildShareText(playlist, tracks)
                    sharePlaylist(shareText)
                }
            }
        }
    }

    private fun buildShareText(playlist: Playlist?, tracks: List<Track>): String {
        return StringBuilder().apply {
            append("${playlist?.name}\n")
            append("${playlist?.description}\n")
            append("[${tracks.size}] треков\n\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n")
            }
        }.toString()
    }

    private fun sharePlaylist(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        menuBottomSheetBehavior.isHideable = true
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.menuButton.setOnClickListener {
            println("Menu button clicked")
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.shareButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.tracks.value?.let { tracks ->
                if (tracks.isEmpty()) {
                    showToast(getString(R.string.no_tracks_to_share))
                } else {
                    val playlist = viewModel.playlist.value
                    val shareText = buildShareText(playlist, tracks)
                    sharePlaylist(shareText)
                }
            }
        }

        binding.deletePlaylistButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist))
            .setMessage(getString(R.string.want_to_delete_playlist))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
                viewModel.deletePlaylist(playlistId)
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Long): PlaylistInfoFragment {
            return PlaylistInfoFragment().apply {
                arguments = bundleOf(ARG_PLAYLIST_ID to playlistId)
            }
        }
    }
}