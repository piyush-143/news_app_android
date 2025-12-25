package com.example.newsapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.newsapp.R;
import com.example.newsapp.ui.home.BreakingFragment;
import com.example.newsapp.ui.home.HomeFragment;
import com.example.newsapp.ui.home.SettingsFragment;
import com.example.newsapp.ui.home.TrendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    private static final String SELECTED_ID_KEY = "selected_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        // --- Handle Back Press Logic ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int selectedItemId = bottomNav.getSelectedItemId();

                // 1. If not on Home tab, go back to Home
                if (selectedItemId != R.id.nav_home) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                } else {
                    // 2. If on Home tab, show Custom Exit Dialog
                    showExitDialog();
                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_trending) {
                selectedFragment = new TrendingFragment();
            } else if (id == R.id.nav_breaking) {
                selectedFragment = new BreakingFragment();
            } else if (id == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            return loadFragment(selectedFragment);
        });

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        } else {
            int selectedId = savedInstanceState.getInt(SELECTED_ID_KEY, R.id.nav_home);
            bottomNav.setSelectedItemId(selectedId);
        }
    }

    // --- UPDATED METHOD: Uses R.layout.dialog_exit ---
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the custom layout we created
        View view = inflater.inflate(R.layout.dialog_exit, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        // IMPORTANT: Make the default dialog window transparent
        // This allows our rounded corner background (bg_dialog_rounded) to be visible
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Handle button clicks from the custom layout
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnExit).setOnClickListener(v -> finish());

        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ID_KEY, bottomNav.getSelectedItemId());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}