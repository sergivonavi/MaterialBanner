package com.sergivonavi.materialbanner.app.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergivonavi.materialbanner.Banner
import com.sergivonavi.materialbanner.app.R
import com.sergivonavi.materialbanner.app.activities.adapter.ItemsAdapter

abstract class BaseSampleActivity : AppCompatActivity() {
    protected lateinit var mBanner: Banner

    @LayoutRes
    protected abstract fun setLayoutView(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayoutView())

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = ItemsAdapter(this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sample, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_toggle_banner) {
            if (mBanner.isShown) {
                mBanner.dismiss()
            } else {
                mBanner.show()
            }
            return true
        }
        return false
    }
}