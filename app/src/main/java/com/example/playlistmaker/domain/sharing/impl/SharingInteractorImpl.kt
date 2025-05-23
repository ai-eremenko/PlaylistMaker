package com.example.playlistmaker.domain.sharing.impl

import android.content.Context
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(context: Context, shareMessage: String) {
        externalNavigator.shareLink(context, shareMessage)
    }

    override fun openTerms(context: Context, termsUrl: String) {
        externalNavigator.openLink(context, termsUrl)
    }

    override fun openSupport(context: Context, emailData: EmailData) {
        externalNavigator.openEmail(context, emailData)
    }
}