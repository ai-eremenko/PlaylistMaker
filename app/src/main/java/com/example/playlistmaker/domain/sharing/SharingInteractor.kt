package com.example.playlistmaker.domain.sharing

import android.content.Context
import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {
    fun shareApp(context: Context, shareMessage: String)
    fun openTerms(context: Context, termsUrl: String)
    fun openSupport(context: Context, emailData: EmailData)
}