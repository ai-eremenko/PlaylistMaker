package com.example.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val _themeLiveData = MutableLiveData<ThemeSettings>()
    val themeLiveData: LiveData<ThemeSettings> = _themeLiveData

    init {
        _themeLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun updateThemeSettings(enabled: Boolean) {
        val newSettings = ThemeSettings(enabled)
        settingsInteractor.updateThemeSetting(newSettings)
        _themeLiveData.value = newSettings
    }

    fun shareApp(context: Context, shareMessage: String) {
        sharingInteractor.shareApp(context, shareMessage)
    }

    fun openTerms(context: Context, termsUrl: String) {
        sharingInteractor.openTerms(context, termsUrl)
    }

    fun openSupport(context: Context, emailData: EmailData) {
        sharingInteractor.openSupport(context, emailData)
    }
}