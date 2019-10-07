package com.sergivonavi.materialbanner.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sergivonavi.materialbanner.app.activities.CustomFontActivity;
import com.sergivonavi.materialbanner.app.activities.FromCodeActivity;
import com.sergivonavi.materialbanner.app.activities.FromLayoutActivity;
import com.sergivonavi.materialbanner.app.activities.GlobalStyleActivity;
import com.sergivonavi.materialbanner.app.activities.ShowcaseActivity;
import com.sergivonavi.materialbanner.app.activities.StyledBannerActivity;
import com.sergivonavi.materialbanner.app.activities.WithPaddingActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn1 = findViewById(R.id.btn_showcase);
        Button btn2 = findViewById(R.id.btn_from_layout);
        Button btn3 = findViewById(R.id.btn_from_activity);
        Button btn4 = findViewById(R.id.btn_styled);
        Button btn5 = findViewById(R.id.btn_global_style);
        Button btn6 = findViewById(R.id.btn_with_padding);
        Button btn7 = findViewById(R.id.btn_custom_fonts);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_showcase:
                intent.setClass(MainActivity.this, ShowcaseActivity.class);
                break;
            case R.id.btn_from_layout:
                intent.setClass(MainActivity.this, FromLayoutActivity.class);
                break;
            case R.id.btn_from_activity:
                intent.setClass(MainActivity.this, FromCodeActivity.class);
                break;
            case R.id.btn_styled:
                intent.setClass(MainActivity.this, StyledBannerActivity.class);
                break;
            case R.id.btn_global_style:
                intent.setClass(MainActivity.this, GlobalStyleActivity.class);
                break;
            case R.id.btn_with_padding:
                intent.setClass(MainActivity.this, WithPaddingActivity.class);
                break;
            case R.id.btn_custom_fonts:
                intent.setClass(MainActivity.this, CustomFontActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }
        return false;
    }
}
