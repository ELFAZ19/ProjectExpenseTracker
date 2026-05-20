package com.cs2018.projectexpensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cs2018.projectexpensetracker.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private DatabaseHelper dbHelper;

    public TaskDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Create
    public long insert(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_PROJECT_ID, task.getProjectId());
        values.put(DatabaseHelper.COLUMN_TASK_NAME, task.getName());
        values.put(DatabaseHelper.COLUMN_TASK_BUDGET, task.getBudget());
        values.put(DatabaseHelper.COLUMN_TASK_STATUS, task.getStatus());

        long id = db.insert(DatabaseHelper.TABLE_TASKS, null, values);
        task.setId(id);
        return id;
    }

    // Read - Get by ID
    public Task getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TASKS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = cursorToTask(cursor);
            cursor.close();
        }
        return task;
    }

    // Read - Get by Project ID
    public List<Task> getByProjectId(long projectId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TASKS,
                null,
                DatabaseHelper.COLUMN_TASK_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectId)},
                null, null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tasks;
    }

    // Read - Get by Project ID and Status
    public List<Task> getByProjectIdAndStatus(long projectId, String status) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TASKS,
                null,
                DatabaseHelper.COLUMN_TASK_PROJECT_ID + " = ? AND " + 
                DatabaseHelper.COLUMN_TASK_STATUS + " = ?",
                new String[]{String.valueOf(projectId), status},
                null, null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tasks;
    }

    // Read - Get All
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TASKS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return tasks;
    }

    // Update
    public int update(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_PROJECT_ID, task.getProjectId());
        values.put(DatabaseHelper.COLUMN_TASK_NAME, task.getName());
        values.put(DatabaseHelper.COLUMN_TASK_BUDGET, task.getBudget());
        values.put(DatabaseHelper.COLUMN_TASK_STATUS, task.getStatus());

        return db.update(
                DatabaseHelper.TABLE_TASKS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())}
        );
    }

    // Delete
    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_TASKS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Get total spent for a task (sum of all expenses)
    public double getTotalSpent(long taskId) {
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

    // Helper method to convert Cursor to Task object
    private Task cursorToTask(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
        int projectIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_PROJECT_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_NAME);
        int budgetIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_BUDGET);
        int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_STATUS);

        return new Task(
                cursor.getLong(idIndex),
                cursor.getLong(projectIdIndex),
                cursor.getString(nameIndex),
                cursor.getDouble(budgetIndex),
                cursor.getString(statusIndex)
        );
    }
}
