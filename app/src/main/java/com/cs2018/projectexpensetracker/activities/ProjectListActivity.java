package com.cs2018.projectexpensetracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.adapters.ProjectAdapter;
import com.cs2018.projectexpensetracker.database.ProjectDAO;
import com.cs2018.projectexpensetracker.models.Project;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ProjectListActivity extends AppCompatActivity implements ProjectAdapter.OnProjectClickListener {
    private RecyclerView rvProjects;
    private ProjectAdapter adapter;
    private ProjectDAO projectDAO;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        // Initialize DAO
        projectDAO = new ProjectDAO(this);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        rvProjects = findViewById(R.id.rv_projects);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        fabAddProject = findViewById(R.id.fab_add_project);

        // Setup RecyclerView
        rvProjects.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB
        fabAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectListActivity.this, AddEditProjectActivity.class);
            startActivity(intent);
        });

        // Load projects
        loadProjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
    }

    private void loadProjects() {
        List<Project> projects = projectDAO.getAll();
        
        if (projects.isEmpty()) {
            rvProjects.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvProjects.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            
            if (adapter == null) {
                adapter = new ProjectAdapter(this, projects, this);
                rvProjects.setAdapter(adapter);
            } else {
                adapter.updateProjects(projects);
            }
        }
    }

    @Override
    public void onProjectClick(Project project) {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("PROJECT_ID", project.getId());
        intent.putExtra("PROJECT_NAME", project.getName());
        startActivity(intent);
    }

    @Override
    public void onProjectLongClick(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(new String[]{"View Summary", "Edit", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // View Summary
                            Intent summaryIntent = new Intent(this, ProjectSummaryActivity.class);
                            summaryIntent.putExtra("PROJECT_ID", project.getId());
                            summaryIntent.putExtra("PROJECT_NAME", project.getName());
                            startActivity(summaryIntent);
                            break;
                        case 1: // Edit
                            Intent editIntent = new Intent(this, AddEditProjectActivity.class);
                            editIntent.putExtra("PROJECT_ID", project.getId());
                            startActivity(editIntent);
                            break;
                        case 2: // Delete
                            confirmDelete(project);
                            break;
                    }
                })
                .show();
    }

    private void confirmDelete(Project project) {
        new AlertDialog.Builder(this)
                .setTitle(String.format(getString(R.string.dialog_delete_title), "project"))
                .setMessage(getString(R.string.dialog_delete_message))
                .setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                    projectDAO.delete(project.getId());
                    Toast.makeText(this, getString(R.string.msg_project_deleted), Toast.LENGTH_SHORT).show();
                    loadProjects();
                })
                .setNegativeButton(getString(R.string.dialog_no), null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
