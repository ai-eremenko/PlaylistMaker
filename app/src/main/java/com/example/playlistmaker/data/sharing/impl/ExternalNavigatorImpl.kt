package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl (
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(link: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(intent, context.getString(R.string.share_app_via)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooserIntent)
    }

    override fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) {
        runCatching {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
                putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                putExtra(Intent.EXTRA_TEXT, emailData.body)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}