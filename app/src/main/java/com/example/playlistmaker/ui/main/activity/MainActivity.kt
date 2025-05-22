package com.example.playlistmaker.ui.main.activity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.main.view_model.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this, MainViewModel.getViewModelFactory(
                Creator.provideMainExternalNavigator(this))
        )[MainViewModel::class.java]

        setupViews()
    }

    private fun setupViews() {
        binding.searchButton.setOnClickListener { viewModel.searchButton() }
        binding.mediaLibraryButton.setOnClickListener { viewModel.mediaLibraryButton() }
        binding.settingsButton.setOnClickListener { viewModel.settingsButton() }
    }
}


