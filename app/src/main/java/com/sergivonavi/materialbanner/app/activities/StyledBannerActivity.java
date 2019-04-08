package com.sergivonavi.materialbanner.app.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import com.sergivonavi.materialbanner.Banner;
import com.sergivonavi.materialbanner.app.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class StyledBannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity_styled_banner);

        // You can do the same from the code
        Banner banner = new Banner.Builder(this).setParent((ViewGroup) findViewById(R.id.root))
                .setIcon(R.drawable.ic_signal_wifi_off_40dp)
                .setMessage(R.string.banner_message3)
                .setLeftButton(R.string.banner_btn_left, null)
                .setRightButton(R.string.banner_btn_right, null)
                .create();

        banner.setBackgroundColor(ContextCompat.getColor(this, R.color.custom_background));
        banner.setMessageTextAppearance(R.style.BannerMessageTextAppearance);
        banner.setMessageTextColor(R.color.custom_message_text);
        banner.setIconTintColor(R.color.custom_icon_tint);
        banner.setButtonsTextAppearance(R.style.BannerButtonsTextAppearance);
        banner.setButtonsTextColor(R.color.custom_buttons_text);
        banner.setButtonsRippleColor(R.color.custom_buttons_text);
        banner.setLineColor(R.color.custom_line);
        banner.setLineOpacity(0.8f);

        // And then show this banner
        banner.show();
    }
}
