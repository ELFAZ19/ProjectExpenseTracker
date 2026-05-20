package com.cs2018.projectexpensetracker.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.database.TaskDAO;
import com.cs2018.projectexpensetracker.models.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditTaskActivity extends AppCompatActivity {
    private TextInputEditText etName, etBudget;
    private RadioGroup rgStatus;
    private Button btnSave, btnCancel;
    private TaskDAO taskDAO;
    private long taskId = -1;
    private long projectId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        taskDAO = new TaskDAO(this);
        projectId = getIntent().getLongExtra("PROJECT_ID", -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.et_task_name);
        etBudget = findViewById(R.id.et_task_budget);
        rgStatus = findViewById(R.id.rg_status);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        if (getIntent().hasExtra("TASK_ID")) {
            isEditMode = true;
            taskId = getIntent().getLongExtra("TASK_ID", -1);
            loadTaskData();
            toolbar.setTitle(getString(R.string.edit_task_title));
        }

        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadTaskData() {
        Task task = taskDAO.getById(taskId);
        if (task != null) {
            etName.setText(task.getName());
            etBudget.setText(String.valueOf(task.getBudget()));
            if (task.isCompleted()) {
                ((RadioButton) findViewById(R.id.rb_completed)).setChecked(true);
            }
        }
    }

    private void saveTask() {
        String name = etName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_task_name_required));
            return;
        }

        if (budgetStr.isEmpty()) {
            etBudget.setError(getString(R.string.error_task_budget_required));
            return;
        }

        double budget = Double.parseDouble(budgetStr);
        if (budget <= 0) {
            etBudget.setError(getString(R.string.error_invalid_budget));
            return;
        }

        String status = ((RadioButton) findViewById(R.id.rb_completed)).isChecked()
                ? Task.STATUS_COMPLETED : Task.STATUS_PENDING;

        if (isEditMode) {
            Task task = taskDAO.getById(taskId);
            task.setName(name);
            task.setBudget(budget);
            task.setStatus(status);
            taskDAO.update(task);
            Toast.makeText(this, getString(R.string.msg_task_updated), Toast.LENGTH_SHORT).show();
        } else {
            Task task = new Task(projectId, name, budget, status);
            taskDAO.insert(task);
            Toast.makeText(this, getString(R.string.msg_task_added), Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
