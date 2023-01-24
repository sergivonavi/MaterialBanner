package com.sergivonavi.materialbanner.app.activities

import android.os.Bundle
import android.view.ViewGroup
import com.sergivonavi.materialbanner.Banner
import com.sergivonavi.materialbanner.app.R
import com.sergivonavi.materialbanner.app.utils.SnackbarHelper

class FromCodeActivity : BaseSampleActivity() {

    override fun setLayoutView(): Int {
        return R.layout.sample_activity_from_code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The root view for the banner
        val rootView = findViewById<ViewGroup>(R.id.root)
        mBanner = Banner.Builder(this).setParent(rootView)
            .setIcon(R.drawable.ic_signal_wifi_off_40dp)
            .setMessage(R.string.banner_message)
            .setLeftButton(R.string.banner_btn_left) { banner ->
                SnackbarHelper.show(rootView, R.string.msg_banner_btnleft)
                banner.dismiss()
            }
            .setRightButton(R.string.banner_btn_right) { banner ->
                SnackbarHelper.show(rootView, R.string.msg_banner_btnright)
                // Dismiss with 0.5 sec delay
                banner.dismiss(500)
            }
            .setOnShowListener { SnackbarHelper.show(rootView, R.string.msg_banner_onshow) }
            .setOnDismissListener { SnackbarHelper.show(rootView, R.string.msg_banner_ondismiss) }
            .create()
    }
}
