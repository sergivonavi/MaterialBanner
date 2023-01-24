package com.sergivonavi.materialbanner.app.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sergivonavi.materialbanner.Banner
import com.sergivonavi.materialbanner.app.R

class StyledBannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_activity_styled_banner)

        // You can do the same from the code
        val banner = Banner.Builder(this).setParent((findViewById<View>(R.id.root) as ViewGroup))
            .setIcon(R.drawable.ic_signal_wifi_off_40dp)
            .setMessage(R.string.banner_message3)
            .setLeftButton(R.string.banner_btn_left, null)
            .setRightButton(R.string.banner_btn_right, null)
            .create()
        banner.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_background))
        banner.setMessageTextAppearance(R.style.BannerMessageTextAppearance)
        banner.setMessageTextColor(R.color.custom_message_text)
        banner.setIconTintColor(R.color.custom_icon_tint)
        // banner.setFont(getString(R.string.font_medium_path));
        // banner.setMessageFont(getString(R.string.font_medium_path));
        // banner.setButtonsFont(getString(R.string.font_medium_path));
        banner.setButtonsTextAppearance(R.style.BannerButtonsTextAppearance)
        banner.setButtonsTextColor(R.color.custom_buttons_text)
        banner.setLeftButtonTextColor(R.color.custom_button_left_text)
        // banner.setRightButtonTextColor(R.color.custom_button_right_text);
        banner.setButtonsRippleColor(R.color.custom_buttons_text)
        banner.setLineColor(R.color.custom_line)
        banner.setLineOpacity(0.8f)

        // And then show this banner
        banner.show()
    }
}