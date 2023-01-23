package com.sergivonavi.materialbanner.app.activities

import android.os.Bundle
import android.view.ViewGroup
import com.sergivonavi.materialbanner.BannerInterface
import com.sergivonavi.materialbanner.app.R
import com.sergivonavi.materialbanner.app.utils.SnackbarHelper

class GlobalStyleActivity : BaseSampleActivity() {

    override fun setLayoutView(): Int {
        return R.layout.sample_activity_global_style
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The magic of styling is done in the activity's theme "CustomTheme"
        val rootView = findViewById<ViewGroup>(R.id.root)
        mBanner = findViewById(R.id.banner)
        mBanner.setLeftButtonListener(object : BannerInterface.OnClickListener {
            override fun onClick(banner: BannerInterface?) {
                SnackbarHelper.show(rootView, R.string.msg_banner_btnleft)
            }
        })
        mBanner.setRightButtonListener(object : BannerInterface.OnClickListener {
            override fun onClick(banner: BannerInterface?) {
                SnackbarHelper.show(rootView, R.string.msg_banner_btnright)
            }
        })
        mBanner.setOnShowListener(object : BannerInterface.OnShowListener {
            override fun onShow() {
                SnackbarHelper.show(rootView, R.string.msg_banner_onshow)
            }
        })
        mBanner.setOnDismissListener(object : BannerInterface.OnDismissListener {
            override fun onDismiss() {
                SnackbarHelper.show(rootView, R.string.msg_banner_onshow)
            }
        })
    }
}