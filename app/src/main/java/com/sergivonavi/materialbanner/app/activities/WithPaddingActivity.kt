package com.sergivonavi.materialbanner.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sergivonavi.materialbanner.app.R

class WithPaddingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_activity_with_padding)

        /*
        // You can do the same thing here

        banner.setContentPaddingStart(dimensionResource);
        banner.setContentPaddingEnd(dimensionResource);

        or

        banner.setContentPaddingStartPx(paddingInPixels);
        banner.setContentPaddingEndPx(paddingInPixels);
        */
    }
}