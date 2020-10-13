package com.argame.activities.settings;

import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.argame.R;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Add back button

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.collapsing_layout_app_bar_account_activity);
        toolBarLayout.setTitle(getTitle());

        // Setup back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Modify alpha while scrolling
        LinearLayout linearLayoutStatusBar = findViewById(R.id.linear_layout_status_bar_account_activity);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout_account_activity);
        ImageView imageViewProfilePhoto = findViewById(R.id.image_view_profile_photo);
        appBarLayout.addOnOffsetChangedListener((layout, verticalOffset) -> {
            float offsetAlpha = (layout.getY() / layout.getTotalScrollRange());
            linearLayoutStatusBar.setAlpha(1);
            imageViewProfilePhoto.setAlpha(1.0f + offsetAlpha * 1.5f);
        });

    }
}