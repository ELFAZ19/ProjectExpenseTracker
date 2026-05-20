package com.cs2018.projectexpensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cs2018.projectexpensetracker.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    private DatabaseHelper dbHelper;

    public ExpenseDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Create
    public long insert(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EXPENSE_TASK_ID, expense.getTaskId());
        values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        values.put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, expense.getDate());

        long id = db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        expense.setId(id);
        return id;
    }

    // Read - Get by ID
    public Expense getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_EXPENSES,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Expense expense = null;
        if (cursor != null && cursor.moveToFirst()) {
            expense = cursorToExpense(cursor);
            cursor.close();
        }
        return expense;
    }

    // Read - Get by Task ID
    public List<Expense> getByTaskId(long taskId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_EXPENSES,
                null,
                DatabaseHelper.COLUMN_EXPENSE_TASK_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null, null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                expenses.add(cursorToExpense(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }

    // Read - Get All
    public List<Expense> getAll() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_EXPENSES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                expenses.add(cursorToExpense(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }

    // Update
    public int update(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EXPENSE_TASK_ID, expense.getTaskId());
        values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        values.put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, expense.getDate());

        return db.update(
                DatabaseHelper.TABLE_EXPENSES,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(expense.getId())}
        );
    }

    // Delete
    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_EXPENSES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Get total amount for a task
    public double getTotalByTask(long taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        
        String query = "SELECT SUM(" + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") as total FROM " +
                DatabaseHelper.TABLE_EXPENSES + " WHERE " + DatabaseHelper.COLUMN_EXPENSE_TASK_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    // Helper method to convert Cursor to Expense object
    private Expense cursorToExpense(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
        int taskIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_TASK_ID);
        int amountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_AMOUNT);
        int descIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION);
        int dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_DATE);

        return new Expense(
                cursor.getLong(idIndex),
                cursor.getLong(taskIdIndex),
                cursor.getDouble(amountIndex),
                cursor.getString(descIndex),
                cursor.getString(dateIndex)
        );
    }
}
