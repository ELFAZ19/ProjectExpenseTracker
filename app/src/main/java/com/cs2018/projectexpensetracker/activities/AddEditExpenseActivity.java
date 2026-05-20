package com.cs2018.projectexpensetracker.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.database.ExpenseDAO;
import com.cs2018.projectexpensetracker.models.Expense;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditExpenseActivity extends AppCompatActivity {
    private TextInputEditText etAmount, etDescription, etDate;
    private Button btnSave, btnCancel;
    private ExpenseDAO expenseDAO;
    private long expenseId = -1;
    private long taskId;
    private boolean isEditMode = false;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        expenseDAO = new ExpenseDAO(this);
        taskId = getIntent().getLongExtra("TASK_ID", -1);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etAmount = findViewById(R.id.et_expense_amount);
        etDescription = findViewById(R.id.et_expense_description);
        etDate = findViewById(R.id.et_expense_date);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // Set current date by default
        etDate.setText(dateFormat.format(calendar.getTime()));

        if (getIntent().hasExtra("EXPENSE_ID")) {
            isEditMode = true;
            expenseId = getIntent().getLongExtra("EXPENSE_ID", -1);
            loadExpenseData();
            toolbar.setTitle(getString(R.string.edit_expense_title));
        }

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveExpense());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadExpenseData() {
        Expense expense = expenseDAO.getById(expenseId);
        if (expense != null) {
            etAmount.setText(String.valueOf(expense.getAmount()));
            etDescription.setText(expense.getDescription());
            etDate.setText(expense.getDate());
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etAmount.setError(getString(R.string.error_expense_amount_required));
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            etAmount.setError(getString(R.string.error_invalid_amount));
            return;
        }

        if (isEditMode) {
            Expense expense = expenseDAO.getById(expenseId);
            expense.setAmount(amount);
            expense.setDescription(description);
            expense.setDate(date);
            expenseDAO.update(expense);
            Toast.makeText(this, getString(R.string.msg_expense_updated), Toast.LENGTH_SHORT).show();
        } else {
            Expense expense = new Expense(taskId, amount, description, date);
            expenseDAO.insert(expense);
            Toast.makeText(this, getString(R.string.msg_expense_added), Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
