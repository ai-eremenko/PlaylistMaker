package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val shareAppButton = findViewById<LinearLayout>(R.id.share_app_button)
        val writeToSupportButton = findViewById<LinearLayout>(R.id.write_to_support_button)
        val userAgreementButton = findViewById<LinearLayout>(R.id.user_agreement_button)


        shareAppButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением через"))
        }

        writeToSupportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            }
            startActivity(emailIntent)
        }

        userAgreementButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.user_agreement_url))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}