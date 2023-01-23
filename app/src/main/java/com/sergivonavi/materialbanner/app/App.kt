package com.sergivonavi.materialbanner.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}