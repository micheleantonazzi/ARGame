package com.argame.activities.settings.account;

import android.os.Bundle;

import com.argame.model.remote_structures.CurrentUser;
import com.argame.model.data_structures.user_data.ListenerUserUpdate;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.argame.R;

public class AccountSettingsActivity extends AppCompatActivity {

    private String nameOldValue;
    private String surnameOldValue;
    private String nicknameOldValue;

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
            AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(task -> finish());
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

        // Modify toolbar's alpha while scrolling
        appBarLayout.addOnOffsetChangedListener((layout, verticalOffset) -> {
            float offsetAlpha = 1.0f + (layout.getY() / layout.getTotalScrollRange()) * 1.5f;
            imageViewProfilePhoto.setAlpha(offsetAlpha);
            textViewUserName.setAlpha(offsetAlpha);
            textViewUserEmail.setAlpha(offsetAlpha);
        });

        // Create onUpdate listener
        ListenerUserUpdate listenerUserUpdate = user -> {
            Log.d("debugg", "aggiorno dati utente");
            if (!textViewUserEmail.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), R.string.profile_data_changed, Toast.LENGTH_LONG).show();
            }

            // Set edit texts old value
            this.nameOldValue = user.getName();
            this.surnameOldValue = user.getSurname();
            this.nicknameOldValue = user.getNickname();

            textViewUserName.setText(user.getName() + " " + user.getSurname());
            textViewUserEmail.setText(user.getEmail());
            editTextName.setText(user.getName());
            editTextSurname.setText(user.getSurname());
            editTextNickName.setText(user.getNickname());
        };

        // Fill activity fields
        listenerUserUpdate.update(CurrentUser.getInstance().getCurrentUser());

        // Pass the listener to the userData
        CurrentUser.getInstance().getCurrentUser().addOnUpdateListenerLifecycle(this, Lifecycle.Event.ON_DESTROY, listenerUserUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem menuItemConfirm = menu.findItem(R.id.menu_item_confirm_profile_data);
        menuItemConfirm.setVisible(false);

        // Add listener to edit texts in order to control the confirm button
        EditText editTextName = findViewById(R.id.edit_text_name);
        EditText editTextSurname = findViewById(R.id.edit_text_surname);
        EditText editTextNickName = findViewById(R.id.edit_text_nickname);

        TextWatcher textWatcherChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editTextName.getText().toString().equals(nameOldValue) ||
                        !editTextSurname.getText().toString().equals(surnameOldValue) ||
                        !editTextNickName.getText().toString().equals(nicknameOldValue))
                    menuItemConfirm.setVisible(true);
                else
                    menuItemConfirm.setVisible(false);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextName.addTextChangedListener(textWatcherChange);
        editTextSurname.addTextChangedListener(textWatcherChange);
        editTextNickName.addTextChangedListener(textWatcherChange);

        menuItemConfirm.setOnMenuItemClickListener(item -> {
            CurrentUser.getInstance().updateUserData(editTextName.getText().toString(), editTextSurname.getText().toString(),
                    editTextNickName.getText().toString());
            menuItemConfirm.setVisible(false);
            nameOldValue = editTextName.getText().toString();
            surnameOldValue = editTextSurname.getText().toString();
            nicknameOldValue = editTextNickName.getText().toString();
            return true;
        });

        return true;
    }

    // Call actions when menu item is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_confirm_profile_data:
                Toast.makeText(getApplicationContext(), R.string.profile_data_changed, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}