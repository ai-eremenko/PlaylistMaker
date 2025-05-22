package com.example.playlistmaker.ui.main.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.main.MainExternalNavigator
import com.example.playlistmaker.domain.main.MainInteractor
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

class MainViewModel (
    private val mainInteractor: MainInteractor,
) : ViewModel () {
    fun searchButton() {
        mainInteractor.searchButton()
    }

    fun mediaLibraryButton() {
        mainInteractor.mediaLibraryButton()
    }

    fun settingsButton() {
        mainInteractor.settingsButton()
    }

    companion object {

        fun getViewModelFactory(externalNavigator: MainExternalNavigator): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(
                        Creator.provideMainInteractor(externalNavigator)
                    ) as T
                }
            }
        }
    }
}