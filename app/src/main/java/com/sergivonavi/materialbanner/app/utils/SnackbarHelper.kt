package com.sergivonavi.materialbanner.app.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarHelper {

    public static void show(View view, int msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

}
