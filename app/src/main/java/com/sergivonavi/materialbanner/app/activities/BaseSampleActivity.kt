package com.sergivonavi.materialbanner.app.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sergivonavi.materialbanner.Banner;
import com.sergivonavi.materialbanner.app.R;
import com.sergivonavi.materialbanner.app.activities.adapter.ItemsAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseSampleActivity extends AppCompatActivity {

    protected Banner mBanner;

    @LayoutRes
    protected abstract int setLayoutView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutView());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ItemsAdapter adapter = new ItemsAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_toggle_banner) {
            if (mBanner.isShown()) {
                mBanner.dismiss();
            } else {
                mBanner.show();
            }
            return true;
        }
        return false;
    }
}
