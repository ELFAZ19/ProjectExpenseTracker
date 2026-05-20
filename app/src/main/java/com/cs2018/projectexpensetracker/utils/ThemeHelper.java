package com.cs2018.projectexpensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeHelper {

    public static final String KEY_THEME_MODE = "theme_mode";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";

    public static void applyTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String themeMode = prefs.getString(KEY_THEME_MODE, THEME_SYSTEM);
        applyTheme(themeMode);
    }

    public static void applyTheme(String themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public static void saveTheme(Context context, String themeMode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(KEY_THEME_MODE, themeMode).apply();
        applyTheme(themeMode);
    }
    
    public static String getSelectedTheme(Context context) {
         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
         return prefs.getString(KEY_THEME_MODE, THEME_SYSTEM);
    }
}
