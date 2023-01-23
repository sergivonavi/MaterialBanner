package com.sergivonavi.materialbanner.app.activities;

import android.os.Bundle;

import com.sergivonavi.materialbanner.app.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WithPaddingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity_with_padding);

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
