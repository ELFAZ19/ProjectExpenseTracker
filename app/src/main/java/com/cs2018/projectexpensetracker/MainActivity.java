package com.cs2018.projectexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs2018.projectexpensetracker.activities.ProjectListActivity;

public class MainActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2500; // 2.5 seconds
    private static final long ANIMATION_DURATION = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        ImageView logo = findViewById(R.id.iv_logo);
        TextView appName = findViewById(R.id.tv_app_name);
        TextView tagline = findViewById(R.id.tv_tagline);

        // Animate views using ViewPropertyAnimator
        logo.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();

        // Animate app name with delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            appName.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        }, 200);

        // Animate tagline with delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tagline.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        }, 400);

        // Navigate to ProjectListActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}