package com.argame.activities.settings;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.argame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Acquire graphic components
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.collapsing_layout_app_bar_account_activity);
        LinearLayout linearLayoutStatusBar = findViewById(R.id.linear_layout_status_bar_account_activity);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout_account_activity);
        ImageView imageViewProfilePhoto = findViewById(R.id.image_view_profile_photo);
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);


        // Add back button
        setSupportActionBar(toolbar);
        toolBarLayout.setTitle(getTitle());

        // Setup back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Modify alpha while scrolling
        appBarLayout.addOnOffsetChangedListener((layout, verticalOffset) -> {
            float offsetAlpha = 1.0f + (layout.getY() / layout.getTotalScrollRange()) * 1.5f;
            imageViewProfilePhoto.setAlpha(offsetAlpha);
            textViewUserName.setAlpha(offsetAlpha);
            textViewUserEmail.setAlpha(offsetAlpha);
        });

        // Retrieve the profile data: name, surname, and the profile image
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            // User's name and email
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            textViewUserName.setText(userName);
            textViewUserEmail.setText(userEmail);
            // User is signed in
            Uri profilePhotoUrl = currentUser.getPhotoUrl();
            if (profilePhotoUrl != null) {
                Log.d("debugg", profilePhotoUrl.toString());
            }
        }

    }
}