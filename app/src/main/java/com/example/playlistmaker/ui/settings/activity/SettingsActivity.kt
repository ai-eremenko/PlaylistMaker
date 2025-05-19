package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.app.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(this@SettingsActivity)
        )[SettingsViewModel::class.java]

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener { finish() }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.shareAppButton.setOnClickListener { viewModel.shareApp() }
        binding.writeToSupportButton.setOnClickListener { viewModel.openSupport() }
        binding.userAgreementButton.setOnClickListener { viewModel.openTerms() }
    }

    private fun observeViewModel() {
        viewModel.themeLiveData.observe(this) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkTheme
            (applicationContext as App).switchTheme(themeSettings.isDarkTheme)
        }
    }
}