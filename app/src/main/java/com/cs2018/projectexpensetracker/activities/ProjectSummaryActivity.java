package com.cs2018.projectexpensetracker.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.database.ProjectDAO;
import com.cs2018.projectexpensetracker.database.TaskDAO;
import com.cs2018.projectexpensetracker.models.Task;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;
import java.io.OutputStream;
import java.io.IOException;
import android.widget.Toast;
import com.cs2018.projectexpensetracker.database.ExpenseDAO;
import com.cs2018.projectexpensetracker.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ProjectSummaryActivity extends AppCompatActivity {
    private PieChart pieChart;
    private BarChart barChart;
    private TextView tvTotalBudget, tvTotalSpent, tvPercentage;
    private long projectId;
    private String projectName;
    private ProjectDAO projectDAO;
    private TaskDAO taskDAO;
    private ExpenseDAO expenseDAO;
    private ActivityResultLauncher<Intent> exportLauncher;
    private StringBuilder csvContentToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_summary);

        projectId = getIntent().getLongExtra("PROJECT_ID", -1);
        projectName = getIntent().getStringExtra("PROJECT_NAME");

        projectDAO = new ProjectDAO(this);
        taskDAO = new TaskDAO(this);
        expenseDAO = new ExpenseDAO(this);
        
        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            saveReportToUri(uri);
                        }
                    }
                });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.summary_title));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pieChart = findViewById(R.id.pie_chart);
        barChart = findViewById(R.id.bar_chart);
        tvTotalBudget = findViewById(R.id.tv_total_budget);
        tvTotalSpent = findViewById(R.id.tv_total_spent);
        tvPercentage = findViewById(R.id.tv_percentage);

        loadData();
    }

    private void loadData() {
        double totalBudget = projectDAO.getTotalBudget(projectId);
        double totalSpent = projectDAO.getTotalSpent(projectId);

        tvTotalBudget.setText(String.format("Br%,.2f", totalBudget));
        tvTotalSpent.setText(String.format("Br%,.2f", totalSpent));

        int percentage = totalBudget > 0 ? (int) ((totalSpent / totalBudget) * 100) : 0;
        tvPercentage.setText(String.format("%d%%", percentage));

        // Load tasks
        List<Task> tasks = taskDAO.getByProjectId(projectId);

        if (!tasks.isEmpty()) {
            setupPieChart(tasks);
            setupBarChart(tasks);
        }
    }

    private void setupPieChart(List<Task> tasks) {
        List<PieEntry> entries = new ArrayList<>();
        
        for (Task task : tasks) {
            double spent = taskDAO.getTotalSpent(task.getId());
            if (spent > 0) {
                entries.add(new PieEntry((float) spent, task.getName()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Task");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Expense\nBreakdown");
        pieChart.setCenterTextSize(14f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart(List<Task> tasks) {
        List<BarEntry> budgetEntries = new ArrayList<>();
        List<BarEntry> spentEntries = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            budgetEntries.add(new BarEntry(i, (float) task.getBudget()));
            spentEntries.add(new BarEntry(i, (float) taskDAO.getTotalSpent(task.getId())));
        }

        BarDataSet budgetDataSet = new BarDataSet(budgetEntries, "Budget");
        budgetDataSet.setColor(ContextCompat.getColor(this, R.color.primary_light));

        BarDataSet spentDataSet = new BarDataSet(spentEntries, "Spent");
        spentDataSet.setColor(ContextCompat.getColor(this, R.color.secondary_light));

        BarData data = new BarData(budgetDataSet, spentDataSet);
        data.setBarWidth(0.35f);
        
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.groupBars(0, 0.3f, 0.05f);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_export_report) {
            initiateExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiateExport() {
        csvContentToSave = generateCsvReport();
        
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "Project_Report_" + projectName.replaceAll("\\s+", "_") + ".csv");
        
        exportLauncher.launch(intent);
    }

    private StringBuilder generateCsvReport() {
        StringBuilder csv = new StringBuilder();
        csv.append("Project Report\n");
        csv.append("Project Name,").append(projectName).append("\n");
        
        double totalBudget = projectDAO.getTotalBudget(projectId);
        double totalSpent = projectDAO.getTotalSpent(projectId);
        
        csv.append("Total Budget,").append(totalBudget).append("\n");
        csv.append("Total Spent,").append(totalSpent).append("\n");
        csv.append("Remaining,").append(totalBudget - totalSpent).append("\n\n");
        
        csv.append("Tasks and Expenses\n");
        csv.append("Task Name,Task Budget,Task Spent,Task Status,Expense Date,Expense Description,Expense Amount\n");
        
        List<Task> tasks = taskDAO.getByProjectId(projectId);
        for (Task task : tasks) {
            List<Expense> expenses = expenseDAO.getByTaskId(task.getId());
            double taskSpent = taskDAO.getTotalSpent(task.getId());
            
            if (expenses.isEmpty()) {
                csv.append(escapeCsv(task.getName())).append(",")
                   .append(task.getBudget()).append(",")
                   .append(taskSpent).append(",")
                   .append(task.getStatus()).append(",,,,\n");
            } else {
                for (Expense expense : expenses) {
                    csv.append(escapeCsv(task.getName())).append(",")
                       .append(task.getBudget()).append(",")
                       .append(taskSpent).append(",")
                       .append(task.getStatus()).append(",")
                       .append(expense.getDate()).append(",")
                       .append(escapeCsv(expense.getDescription())).append(",")
                       .append(expense.getAmount()).append("\n");
                }
            }
        }
        return csv;
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    private void saveReportToUri(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null && csvContentToSave != null) {
                outputStream.write(csvContentToSave.toString().getBytes());
                Toast.makeText(this, "Report saved successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save report", Toast.LENGTH_SHORT).show();
        }
    }
}
