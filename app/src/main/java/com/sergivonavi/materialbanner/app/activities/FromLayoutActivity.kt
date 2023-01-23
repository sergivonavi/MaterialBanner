package com.sergivonavi.materialbanner.app.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import com.sergivonavi.materialbanner.BannerInterface;
import com.sergivonavi.materialbanner.app.R;
import com.sergivonavi.materialbanner.app.utils.SnackbarHelper;

import androidx.annotation.Nullable;

public class FromLayoutActivity extends BaseSampleActivity {

    @Override
    protected int setLayoutView() {
        return R.layout.sample_activity_from_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewGroup rootView = findViewById(R.id.root);

        mBanner = findViewById(R.id.banner);

        mBanner.setLeftButtonListener(new BannerInterface.OnClickListener() {
            @Override
            public void onClick(BannerInterface banner) {
                SnackbarHelper.show(rootView, R.string.msg_banner_btnleft);
            }
        });
        mBanner.setRightButtonListener(new BannerInterface.OnClickListener() {
            @Override
            public void onClick(BannerInterface banner) {
                SnackbarHelper.show(rootView, R.string.msg_banner_btnright);
            }
        });
        mBanner.setOnShowListener(new BannerInterface.OnShowListener() {
            @Override
            public void onShow() {
                SnackbarHelper.show(rootView, R.string.msg_banner_onshow);
            }
        });
        mBanner.setOnDismissListener(new BannerInterface.OnDismissListener() {
            @Override
            public void onDismiss() {
                SnackbarHelper.show(rootView, R.string.msg_banner_ondismiss);
            }
        });
    }
}
