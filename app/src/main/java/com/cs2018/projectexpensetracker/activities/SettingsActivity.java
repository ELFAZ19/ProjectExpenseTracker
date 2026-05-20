package com.cs2018.projectexpensetracker.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.database.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {
    private Button btnClearData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = DatabaseHelper.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings_title));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupThemeSettings();

        btnClearData = findViewById(R.id.btn_clear_data);
        btnClearData.setOnClickListener(v -> confirmClearData());
    }

    private void setupThemeSettings() {
        android.widget.RadioGroup rgTheme = findViewById(R.id.rg_theme);
        android.widget.RadioButton rbLight = findViewById(R.id.rb_light);
        android.widget.RadioButton rbDark = findViewById(R.id.rb_dark);
        android.widget.RadioButton rbSystem = findViewById(R.id.rb_system);

        String currentTheme = com.cs2018.projectexpensetracker.utils.ThemeHelper.getSelectedTheme(this);
        switch (currentTheme) {
            case com.cs2018.projectexpensetracker.utils.ThemeHelper.THEME_LIGHT:
                rbLight.setChecked(true);
                break;
            case com.cs2018.projectexpensetracker.utils.ThemeHelper.THEME_DARK:
                rbDark.setChecked(true);
                break;
            case com.cs2018.projectexpensetracker.utils.ThemeHelper.THEME_SYSTEM:
            default:
                rbSystem.setChecked(true);
                break;
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedTheme = com.cs2018.projectexpensetracker.utils.ThemeHelper.THEME_SYSTEM;
            if (checkedId == R.id.rb_light) {
                selectedTheme = com.cs2018.projectexpensetracker.utils.ThemeHelper.THEME_LIGHT;
            } else if (checkedId == R.id.rb_dark) {
                selectedTheme = com.cs2018.projectexpensetracker.utils.ThemeHelper.THEME_DARK;
            }
            com.cs2018.projectexpensetracker.utils.ThemeHelper.saveTheme(this, selectedTheme);
        });
    }

    private void confirmClearData() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.clear_data_confirm))
                .setMessage(getString(R.string.clear_data_warning))
                .setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                    dbHelper.clearAllData();
                    Toast.makeText(this, getString(R.string.msg_data_cleared), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(getString(R.string.dialog_no), null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
