package com.example.playlistmaker.ui.media_library.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.example.playlistmaker.ui.media_library.adapter.MediaLibraryPagerAdapter
import com.example.playlistmaker.ui.media_library.view_model.MediaLibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    private var tabMediator: TabLayoutMediator? = null
    private val viewModel by viewModel<MediaLibraryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupBackButton()
    }

    private fun setupViewPager() {
        val tabsData = viewModel.getTabsData()
        binding.viewPager.adapter = MediaLibraryPagerAdapter(this)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(tabsData[position].titleResId)
        }.also {
            it.attach()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroy()
    }
}