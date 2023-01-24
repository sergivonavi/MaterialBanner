package com.sergivonavi.materialbanner.app.activities

import android.os.Bundle
import android.view.ViewGroup
import com.sergivonavi.materialbanner.app.R
import com.sergivonavi.materialbanner.app.utils.SnackbarHelper

class FromLayoutActivity : BaseSampleActivity() {

    override fun setLayoutView(): Int {
        return R.layout.sample_activity_from_layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = findViewById<ViewGroup>(R.id.root)

        mBanner = findViewById(R.id.banner)
        mBanner.setLeftButtonListener { banner ->
            SnackbarHelper.show(rootView, R.string.msg_banner_btnleft)
            banner.dismiss()
        }
        mBanner.setRightButtonListener { banner ->
            SnackbarHelper.show(rootView, R.string.msg_banner_btnright)
            // Dismiss with 0.5 sec delay
            banner.dismiss(500)
        }
        mBanner.setOnShowListener { SnackbarHelper.show(rootView, R.string.msg_banner_onshow) }
        mBanner.setOnDismissListener {
            SnackbarHelper.show(rootView, R.string.msg_banner_ondismiss)
        }
    }
}