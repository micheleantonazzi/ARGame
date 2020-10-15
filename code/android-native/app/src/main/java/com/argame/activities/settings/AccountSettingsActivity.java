package com.argame.activities.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.argame.activities.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.argame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AccountSettingsActivity extends AppCompatActivity {

    private String userNameOldValue;
    private String userNicknameOldValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Acquire graphic components
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.collapsing_layout_app_bar_account_activity);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout_account_activity);
        ImageView imageViewProfilePhoto = findViewById(R.id.image_view_profile_photo);
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);
        EditText editTextName = findViewById(R.id.edit_text_name);
        EditText editTextSurname = findViewById(R.id.edit_text_surname);
        EditText editTextNickName = findViewById(R.id.edit_text_nickname);
        Button buttonLogout = findViewById(R.id.button_logout);

        buttonLogout.setOnClickListener(view -> {
            AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(task -> {
                finish();
            });
        });

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
            Log.d("debugg",currentUser.getUid());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.findItem(R.id.menu_item_confirm).setVisible(false);
        return true;
    }

    // Call actions when menu item is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_confirm:
                Toast.makeText(getApplicationContext(), R.string.profile_data_changed, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}