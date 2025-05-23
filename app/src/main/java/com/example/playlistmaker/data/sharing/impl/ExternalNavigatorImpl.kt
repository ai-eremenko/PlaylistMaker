package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl () : ExternalNavigator {

    override fun shareLink(context: Context, link: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_app_via)))
    }

    override fun openLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    override fun openEmail(context: Context, emailData: EmailData) {
        runCatching {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
                putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                putExtra(Intent.EXTRA_TEXT, emailData.body)
            }
            context.startActivity(intent)
        }
    }
}