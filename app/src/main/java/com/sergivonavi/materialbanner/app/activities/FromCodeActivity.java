package com.sergivonavi.materialbanner.app.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import com.sergivonavi.materialbanner.Banner;
import com.sergivonavi.materialbanner.BannerInterface;
import com.sergivonavi.materialbanner.app.R;
import com.sergivonavi.materialbanner.app.utils.SnackbarHelper;

import androidx.annotation.Nullable;

public class FromCodeActivity extends BaseSampleActivity {

    @Override
    protected int setLayoutView() {
        return R.layout.sample_activity_from_code;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The root view for the banner
        final ViewGroup rootView = findViewById(R.id.root);

        mBanner = new Banner.Builder(this).setParent(rootView)
                .setIcon(R.drawable.ic_signal_wifi_off_40dp)
                .setMessage(R.string.banner_message)
                .setLeftButton(R.string.banner_btn_left, new BannerInterface.OnClickListener() {
                    @Override
                    public void onClick(BannerInterface banner) {
                        SnackbarHelper.show(rootView, R.string.msg_banner_btnleft);
                        banner.dismiss();
                    }
                })
                .setRightButton(R.string.banner_btn_right, new BannerInterface.OnClickListener() {
                    @Override
                    public void onClick(BannerInterface banner) {
                        SnackbarHelper.show(rootView, R.string.msg_banner_btnright);
                        // Dismiss with 0.5 sec delay
                        banner.dismiss(500);
                    }
                })
                .setOnShowListener(new BannerInterface.OnShowListener() {
                    @Override
                    public void onShow() {
                        SnackbarHelper.show(rootView, R.string.msg_banner_onshow);
                    }
                })
                .setOnDismissListener(new BannerInterface.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        SnackbarHelper.show(rootView, R.string.msg_banner_ondismiss);
                    }
                })
                .create();
    }
}
