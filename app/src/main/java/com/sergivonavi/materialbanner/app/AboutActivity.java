package com.sergivonavi.materialbanner.app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView description = findViewById(R.id.description);
        description.setText(
                Html.fromHtml(getString(R.string.library_MaterialBanner_libraryDescription)));
        TextView appVersion = findViewById(R.id.version_app);
        appVersion.setText(String.format(getString(R.string.about_app_version),
                getString(R.string.materialbanner_app_version)));
        TextView libVersion = findViewById(R.id.version_lib);
        libVersion.setText(String.format(getString(R.string.about_lib_version),
                getString(R.string.materialbanner_lib_version)));
    }

}
