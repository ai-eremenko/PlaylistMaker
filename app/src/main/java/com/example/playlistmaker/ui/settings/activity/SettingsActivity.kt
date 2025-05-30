package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.app.MyApplication
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener { finish() }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.shareAppButton.setOnClickListener {
            viewModel.shareApp(getString(R.string.share_app_message))
        }

        binding.writeToSupportButton.setOnClickListener {
            viewModel.openSupport(
                EmailData(
                    email = getString(R.string.mail),
                    subject = getString(R.string.support_email_subject),
                    body = getString(R.string.support_email_body)
                )
            )
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms(getString(R.string.user_agreement_url))
        }
    }

    private fun observeViewModel() {
        viewModel.themeLiveData.observe(this) { themeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkTheme
            (applicationContext as MyApplication).switchTheme(themeSettings.isDarkTheme)
        }
    }
}