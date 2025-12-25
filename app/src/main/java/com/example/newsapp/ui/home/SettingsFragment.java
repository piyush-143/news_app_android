package com.example.newsapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.data.DbHelper;
import com.example.newsapp.ui.auth.LoginActivity;
import com.example.newsapp.ui.profile.UserProfileActivity;
import com.example.newsapp.utils.CustomSnackBar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private ImageView imgProfileToolbar;
    private Button btnAuthAction;
    private DbHelper dbHelper;
    private SharedPreferences userPrefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        dbHelper = new DbHelper(getContext());
        userPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        imgProfileToolbar = view.findViewById(R.id.imgProfileToolbar);
        SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnAuthAction = view.findViewById(R.id.btnAuthAction);
        TextView btnHelp = view.findViewById(R.id.btnHelp);
        TextView btnPrivacy = view.findViewById(R.id.btnPrivacy);
        TextView btnShare = view.findViewById(R.id.btnShare);

        // --- Dark Mode ---
        SharedPreferences settingsPrefs = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean isDark = settingsPrefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDark);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsPrefs.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // --- Profile Navigation ---
        imgProfileToolbar.setOnClickListener(v -> {
            String email = userPrefs.getString("email", null);
            if (email != null) {
                startActivity(new Intent(getContext(), UserProfileActivity.class));
            } else {
                // UPDATED: Info Snackbar
                if (getView() != null)
                    CustomSnackBar.showInfo(getContext(), getView(), "Please Login to view profile");
            }
        });

        // --- Support Links (Placeholder) ---
        btnHelp.setOnClickListener(v -> {
            if (getView() != null) CustomSnackBar.showInfo(getContext(), getView(), "Help & Support: Coming Soon");
        });

        btnPrivacy.setOnClickListener(v -> {
            if (getView() != null) CustomSnackBar.showInfo(getContext(), getView(), "Privacy Policy: Coming Soon");
        });

        btnShare.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing News App!");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        String email = userPrefs.getString("email", null);

        if (email != null) {
            // Logged In State
            btnAuthAction.setText("Sign Out");
            btnAuthAction.setOnClickListener(v -> {
                userPrefs.edit().clear().apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });

            Cursor cursor = dbHelper.getUserDetails(email);
            if (cursor != null && cursor.moveToFirst()) {
                int imageIndex = cursor.getColumnIndex("Image");
                if (imageIndex != -1) {
                    String imagePath = cursor.getString(imageIndex);
                    if (imagePath != null && !imagePath.isEmpty()) {
                        Glide.with(this).load(imagePath).into(imgProfileToolbar);
                    }
                }
                cursor.close();
            }
        } else {
            // Guest State
            btnAuthAction.setText("Login");
            btnAuthAction.setTextColor(getResources().getColor(R.color.indigo_primary, null));
            btnAuthAction.setBackgroundColor(getResources().getColor(android.R.color.white, null));
            btnAuthAction.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });

            imgProfileToolbar.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}