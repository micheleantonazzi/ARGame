package com.argame.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.argame.R;
import com.argame.activities.fragments.FragmentFriendsDirections;
import com.argame.activities.fragments.FragmentGamesDirections;
import com.argame.activities.settings.AccountSettingsActivity;
import com.argame.activities.settings.ApplicationSettingsActivity;
import com.argame.utilities.ThemeSelector;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authService;
    private BottomNavigationView bottomNavigationView;
    private NavController navControllerBottomNavigation;
    private static final int SIGN_IN_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.authService = FirebaseAuth.getInstance();

        // Get the nav controller for the fragment that manage the bottom navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_host_fragment);
        this.navControllerBottomNavigation = navHostFragment.getNavController();

        // Connect the app bar with the bottom navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_fragment_games, R.id.navigation_fragment_friends)
                .build();
        NavigationUI.setupActionBarWithNavController(this, this.navControllerBottomNavigation, appBarConfiguration);

        // Setup the bottom navigation view
        this.bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(bottomNavigationView.getSelectedItemId() == item.getItemId())
                    return false;

                switch (item.getItemId()){
                    case R.id.item_bottom_menu_games:

                        navControllerBottomNavigation.navigate(
                                FragmentFriendsDirections.actionNavigationFragmentFriendsToNavigationFragmentGames()

                        );
                        return true;
                    case R.id.item_bottom_menu_friends:
                        navControllerBottomNavigation.navigate(
                                FragmentGamesDirections.actionNavigationFragmentGamesToNavigationFragmentFriends()
                        );
                        return true;
                }
                return false;
            }
        });



        // Set theme according to the user preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        new ThemeSelector().selectTheme(preferences.getString("list_preference_theme", "0"));
    }

    // Check if the user is logged, otherwise load the login intent
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = this.authService.getCurrentUser();
        if (currentUser == null)
            createSignInIntent();
    }

    @Override
    public void onBackPressed() {
        if(this.bottomNavigationView.getSelectedItemId() == R.id.item_bottom_menu_games)
            finish();

        this.bottomNavigationView.setSelectedItemId(R.id.item_bottom_menu_games);
    }

    // Create the login intend provided by Firebase
    public void createSignInIntent(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                SIGN_IN_CODE);
    }

    // Obtain the result of the Firebase's login intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {

            }
        }
    }

    // Insert the main menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    // Call actions when menu item is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_account_settings:
                startActivity(new Intent(MainActivity.this, AccountSettingsActivity.class));
                return true;
            case R.id.menu_item_application_settings:
                startActivity(new Intent(MainActivity.this, ApplicationSettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}