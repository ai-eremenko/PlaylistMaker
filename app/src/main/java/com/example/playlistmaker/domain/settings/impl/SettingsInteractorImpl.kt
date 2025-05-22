package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            isDarkTheme = settingsRepository.isDarkThemeEnabled()
        )
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.setDarkThemeEnabled(settings.isDarkTheme)
    }
}