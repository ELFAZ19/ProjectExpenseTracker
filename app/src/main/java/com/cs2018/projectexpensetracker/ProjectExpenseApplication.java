package com.cs2018.projectexpensetracker;

import android.app.Application;

import com.cs2018.projectexpensetracker.utils.ThemeHelper;

public class ProjectExpenseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize theme based on saved preferences
        ThemeHelper.applyTheme(this);
    }
}
