package com.example.playlistmaker.domain.sharing.impl

import android.content.res.Resources
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val resources: Resources
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(resources.getString(R.string.share_app_message))
    }

    override fun openTerms() {
        externalNavigator.openLink(resources.getString(R.string.user_agreement_url))
    }

    override fun openSupport() {
        externalNavigator.openEmail(
            EmailData(
                email = resources.getString(R.string.mail),
                subject = resources.getString(R.string.support_email_subject),
                body = resources.getString(R.string.support_email_body)
            )
        )
    }
}