package com.cs2018.projectexpensetracker.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.database.ProjectDAO;
import com.cs2018.projectexpensetracker.models.Project;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditProjectActivity extends AppCompatActivity {
    private TextInputEditText etName, etDescription, etStartDate, etEndDate;
    private Button btnSave, btnCancel;
    private ProjectDAO projectDAO;
    private long projectId = -1;
    private boolean isEditMode = false;
    private Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_project);

        // Initialize DAO
        projectDAO = new ProjectDAO(this);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        etName = findViewById(R.id.et_project_name);
        etDescription = findViewById(R.id.et_project_description);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Initialize calendars
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        // Check if edit mode
        if (getIntent().hasExtra("PROJECT_ID")) {
            isEditMode = true;
            projectId = getIntent().getLongExtra("PROJECT_ID", -1);
            loadProjectData();
            toolbar.setTitle(getString(R.string.edit_project_title));
        }

        // Setup date pickers
        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));

        // Button listeners
        btnSave.setOnClickListener(v -> saveProject());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadProjectData() {
        Project project = projectDAO.getById(projectId);
        if (project != null) {
            etName.setText(project.getName());
            etDescription.setText(project.getDescription());
            etStartDate.setText(project.getStartDate());
            etEndDate.setText(project.getEndDate());
        }
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String formattedDate = dateFormat.format(calendar.getTime());
                    if (isStartDate) {
                        etStartDate.setText(formattedDate);
                    } else {
                        etEndDate.setText(formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveProject() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_project_name_required));
            etName.requestFocus();
            return;
        }

        // Create or update project
        Project project;
        if (isEditMode) {
            project = projectDAO.getById(projectId);
            project.setName(name);
            project.setDescription(description);
            project.setStartDate(startDate);
            project.setEndDate(endDate);
            projectDAO.update(project);
            Toast.makeText(this, getString(R.string.msg_project_updated), Toast.LENGTH_SHORT).show();
        } else {
            project = new Project(name, description, startDate, endDate);
            projectDAO.insert(project);
            Toast.makeText(this, getString(R.string.msg_project_added), Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
