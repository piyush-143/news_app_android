package com.example.newsapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.newsapp.R;
import com.google.android.material.snackbar.Snackbar;

public class CustomSnackBar {

    private static void show(View view, String message, int colorHex, int iconRes) {
        if (view == null) return;

        // Create standard Snackbar
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);

        // Transparent background to show our custom card
        // We set the SNACKBAR'S view to transparent so only our CUSTOM view shows with color
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // Remove padding
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);

        // Inflate Custom Layout
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        @SuppressLint("InflateParams") View customView = inflater.inflate(R.layout.layout_custom_snackbar, null);

        // Bind Data
        View container = customView.findViewById(R.id.snackContainer);
        ImageView icon = customView.findViewById(R.id.snackIcon);
        TextView text = customView.findViewById(R.id.snackMessage);

        // Apply the specific color (Green/Red/Indigo) to the card background
        container.setBackgroundTintList(ColorStateList.valueOf(colorHex));
        icon.setImageResource(iconRes);
        text.setText(message);

        // Add to Snackbar
        snackbarLayout.addView(customView, 0);
        snackbar.show();
    }

    public static void showSuccess(Context context, View view, String message) {
        // Green
        show(view, message, Color.parseColor("#4CAF50"), R.drawable.ic_check_circle);
    }

    public static void showError(Context context, View view, String message) {
        // Red
        show(view, message, Color.parseColor("#EF5350"), R.drawable.ic_error_outline);
    }

    public static void showInfo(Context context, View view, String message) {
        // Indigo
        show(view, message, Color.parseColor("#5C6BC0"), R.drawable.ic_info_outline);
    }
}