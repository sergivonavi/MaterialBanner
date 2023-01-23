package com.sergivonavi.materialbanner.app

import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val description = findViewById<TextView>(R.id.description)
        description.text =
            HtmlCompat.fromHtml(
                getString(com.sergivonavi.materialbanner.R.string.library_MaterialBanner_libraryDescription),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        val appVersion = findViewById<TextView>(R.id.version_app)
        appVersion.text = String.format(
            getString(R.string.about_app_version),
            getString(R.string.materialbanner_app_version)
        )
        val libVersion = findViewById<TextView>(R.id.version_lib)
        libVersion.text = String.format(
            getString(R.string.about_lib_version),
            getString(com.sergivonavi.materialbanner.R.string.materialbanner_lib_version)
        )
    }
}