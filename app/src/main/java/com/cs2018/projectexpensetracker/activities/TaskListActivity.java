package com.cs2018.projectexpensetracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.adapters.TaskAdapter;
import com.cs2018.projectexpensetracker.database.TaskDAO;
import com.cs2018.projectexpensetracker.models.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private RecyclerView rvTasks;
    private TaskAdapter adapter;
    private TaskDAO taskDAO;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddTask;
    private long projectId;
    private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Get project ID from intent
        projectId = getIntent().getLongExtra("PROJECT_ID", -1);
        projectName = getIntent().getStringExtra("PROJECT_NAME");

        // Initialize DAO
        taskDAO = new TaskDAO(this);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(projectName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        rvTasks = findViewById(R.id.rv_tasks);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        fabAddTask = findViewById(R.id.fab_add_task);

        // Setup RecyclerView
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
        });

        // Load tasks
        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        List<Task> tasks = taskDAO.getByProjectId(projectId);
        
        if (tasks.isEmpty()) {
            rvTasks.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvTasks.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            
            if (adapter == null) {
                adapter = new TaskAdapter(this, tasks, this);
                rvTasks.setAdapter(adapter);
            } else {
                adapter.updateTasks(tasks);
            }
        }
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, ExpenseListActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        intent.putExtra("TASK_NAME", task.getName());
        intent.putExtra("TASK_BUDGET", task.getBudget());
        startActivity(intent);
    }

    @Override
    public void onTaskLongClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) { // Edit
                        Intent editIntent = new Intent(this, AddEditTaskActivity.class);
                        editIntent.putExtra("TASK_ID", task.getId());
                        editIntent.putExtra("PROJECT_ID", projectId);
                        startActivity(editIntent);
                    } else { // Delete
                        confirmDelete(task);
                    }
                })
                .show();
    }

    private void confirmDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(String.format(getString(R.string.dialog_delete_title), "task"))
                .setMessage(getString(R.string.dialog_delete_message))
                .setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                    taskDAO.delete(task.getId());
                    Toast.makeText(this, getString(R.string.msg_task_deleted), Toast.LENGTH_SHORT).show();
                    loadTasks();
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
