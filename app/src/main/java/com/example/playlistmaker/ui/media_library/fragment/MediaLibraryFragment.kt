package com.example.playlistmaker.ui.media_library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audio_player.fragment.AudioPlayerFragment
import com.example.playlistmaker.ui.media_library.adapter.MediaLibraryPagerAdapter
import com.example.playlistmaker.ui.media_library.view_model.MediaLibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private lateinit var binding: FragmentMediaLibraryBinding
    private var tabsMediator: TabLayoutMediator? = null
    private val viewModel by viewModel<MediaLibraryViewModel>()

    companion object {
        private const val ARGS_FAVOURITE_ID = "favourite_id"
        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(favouriteId: String, playlistId: String): Bundle =
            bundleOf(ARGS_FAVOURITE_ID to favouriteId,
                ARGS_PLAYLIST_ID to playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabsData = viewModel.getTabsData()
        binding.viewPager.adapter = MediaLibraryPagerAdapter(this)

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(tabsData[position].titleResId)
        }.also {
            it.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabsMediator?.detach()
    }

    fun navigateToPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_global_audioPlayerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }
}