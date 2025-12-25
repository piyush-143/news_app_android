package com.example.newsapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AppConstants {
    // API Keys
    private static final String API_KEY_1 = "f30ea56f80960f899e470e0224104153";
    private static final String API_KEY_2 = "83bd6d4f8b3a36967e6c25322312c919";
    private static final String API_KEY_3 = "8da6659444afe897a93038ad8e0d8340";
    private static final String API_KEY_4 = "05f37a41796c6ca964e6701bb6c0ebca";

    public static final String BASE_URL = "https://gnews.io/api/v4/";

    // Logic to match Flutter's AppUrls structure
    public static String getApiKeyForCategory(String category) {
        switch (category.toLowerCase()) {
            // Key 2 Categories
            case "business":
            case "sports":
            case "general": // 'recent'
                return API_KEY_2;

            // Key 3 Categories
            case "technology":
            case "science":
            case "health":
                return API_KEY_3;

            // Key 4 Categories
            case "entertainment":
            case "nation":
            case "world":
                return API_KEY_4;

            // Key 1 (Default for Search: featured, trending, breaking, gaming)
            default:
                return API_KEY_1;
        }
    }

    public static String getApiKey() {
        return API_KEY_1;
    }

    // Date Formatter
    public static String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(dateString);

            // Add +5:30 for IST (matching your Flutter DateFormatter)
            // Note: In real apps, it's better to use local device time, but we follow your logic here.
            long timeInMillis = date.getTime() + (5 * 60 * 60 * 1000) + (30 * 60 * 1000);
            Date istDate = new Date(timeInMillis);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            return outputFormat.format(istDate);
        } catch (Exception e) {
            return dateString;
        }
    }
}