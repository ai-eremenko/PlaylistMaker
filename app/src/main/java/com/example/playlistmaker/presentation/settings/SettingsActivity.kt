package com.example.playlistmaker.presentation.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.app.App
import com.example.playlistmaker.app.Constants
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

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
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean(Constants.DARK_THEME, checked)
                apply()
        }
        }

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