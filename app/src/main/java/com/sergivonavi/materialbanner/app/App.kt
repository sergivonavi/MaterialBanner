package com.sergivonavi.materialbanner.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
