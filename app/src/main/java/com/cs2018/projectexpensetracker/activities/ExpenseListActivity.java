package com.cs2018.projectexpensetracker.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.adapters.ExpenseAdapter;
import com.cs2018.projectexpensetracker.database.ExpenseDAO;
import com.cs2018.projectexpensetracker.models.Expense;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExpenseListActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {
    private RecyclerView rvExpenses;
    private ExpenseAdapter adapter;
    private ExpenseDAO expenseDAO;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddExpense;
    private TextView tvBudget, tvSpent, tvStatus;
    private ProgressBar progressBar;
    private long taskId;
    private String taskName;
    private double taskBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        taskId = getIntent().getLongExtra("TASK_ID", -1);
        taskName = getIntent().getStringExtra("TASK_NAME");
        taskBudget = getIntent().getDoubleExtra("TASK_BUDGET", 0);

        expenseDAO = new ExpenseDAO(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(taskName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvExpenses = findViewById(R.id.rv_expenses);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        fabAddExpense = findViewById(R.id.fab_add_expense);
        tvBudget = findViewById(R.id.tv_budget);
        tvSpent = findViewById(R.id.tv_spent);
        tvStatus = findViewById(R.id.tv_status);
        progressBar = findViewById(R.id.progress_bar);

        rvExpenses.setLayoutManager(new LinearLayoutManager(this));

        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseListActivity.this, AddEditExpenseActivity.class);
            intent.putExtra("TASK_ID", taskId);
            startActivity(intent);
        });

        loadExpenses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        List<Expense> expenses = expenseDAO.getByTaskId(taskId);
        double totalSpent = expenseDAO.getTotalByTask(taskId);

        tvBudget.setText(String.format("Br%,.2f", taskBudget));
        tvSpent.setText(String.format("Br%,.2f", totalSpent));

        int progress = taskBudget > 0 ? (int) ((totalSpent / taskBudget) * 100) : 0;
        progressBar.setMax(100);
        progressBar.setProgress(progress);

        int progressColor;
        String status;
        if (progress < 80) {
            progressColor = ContextCompat.getColor(this, R.color.budget_good);
            status = getString(R.string.budget_status_good);
        } else if (progress <= 100) {
            progressColor = ContextCompat.getColor(this, R.color.budget_warning);
            status = getString(R.string.budget_status_warning);
        } else {
            progressColor = ContextCompat.getColor(this, R.color.budget_danger);
            status = getString(R.string.budget_status_danger);
        }
        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(progressColor));
        tvStatus.setText(status);
        tvStatus.setTextColor(progressColor);

        if (expenses.isEmpty()) {
            rvExpenses.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvExpenses.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            
            if (adapter == null) {
                adapter = new ExpenseAdapter(this, expenses, this);
                rvExpenses.setAdapter(adapter);
            } else {
                adapter.updateExpenses(expenses);
            }
        }
    }

    @Override
    public void onExpenseLongClick(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent editIntent = new Intent(this, AddEditExpenseActivity.class);
                        editIntent.putExtra("EXPENSE_ID", expense.getId());
                        editIntent.putExtra("TASK_ID", taskId);
                        startActivity(editIntent);
                    } else {
                        confirmDelete(expense);
                    }
                })
                .show();
    }

    private void confirmDelete(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle(String.format(getString(R.string.dialog_delete_title), "expense"))
                .setMessage(getString(R.string.dialog_delete_message))
                .setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
                    expenseDAO.delete(expense.getId());
                    Toast.makeText(this, getString(R.string.msg_expense_deleted), Toast.LENGTH_SHORT).show();
                    loadExpenses();
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
