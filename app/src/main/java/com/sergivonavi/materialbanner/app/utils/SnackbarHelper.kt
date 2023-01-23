package com.sergivonavi.materialbanner.app.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarHelper {
    fun show(view: View?, msg: Int) {
        Snackbar.make(view!!, msg, Snackbar.LENGTH_SHORT).show()
    }
}