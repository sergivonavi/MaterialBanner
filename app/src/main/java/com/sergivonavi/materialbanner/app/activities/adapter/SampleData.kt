package com.sergivonavi.materialbanner.app.activities.adapter

internal object SampleData {
    private const val BASE = "file:///android_asset/pics/"
    private const val EXT = ".jpg"

    @JvmField
    val ASSETS = arrayOf(
        BASE + "1" + EXT, BASE + "2" + EXT, BASE + "3" + EXT, BASE + "4" + EXT,
        BASE + "5" + EXT, BASE + "6" + EXT, BASE + "7" + EXT, BASE + "8" + EXT,
        BASE + "9" + EXT, BASE + "10" + EXT
    )
}