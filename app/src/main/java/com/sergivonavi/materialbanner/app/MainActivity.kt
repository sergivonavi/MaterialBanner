package com.sergivonavi.materialbanner.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.sergivonavi.materialbanner.app.activities.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn1 = findViewById<Button>(R.id.btn_showcase)
        val btn2 = findViewById<Button>(R.id.btn_from_layout)
        val btn3 = findViewById<Button>(R.id.btn_from_activity)
        val btn4 = findViewById<Button>(R.id.btn_styled)
        val btn5 = findViewById<Button>(R.id.btn_global_style)
        val btn6 = findViewById<Button>(R.id.btn_with_padding)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
        btn5.setOnClickListener(this)
        btn6.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intent = Intent()
        when (v.id) {
            R.id.btn_showcase -> intent.setClass(this@MainActivity, ShowcaseActivity::class.java)
            R.id.btn_from_layout -> intent.setClass(
                this@MainActivity,
                FromLayoutActivity::class.java
            )
            R.id.btn_from_activity -> intent.setClass(
                this@MainActivity,
                FromCodeActivity::class.java
            )
            R.id.btn_styled -> intent.setClass(this@MainActivity, StyledBannerActivity::class.java)
            R.id.btn_global_style -> intent.setClass(
                this@MainActivity,
                GlobalStyleActivity::class.java
            )
            R.id.btn_with_padding -> intent.setClass(
                this@MainActivity,
                WithPaddingActivity::class.java
            )
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_about) {
            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            return true
        }
        return false
    }
}