package com.cs2018.projectexpensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cs2018.projectexpensetracker.models.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private DatabaseHelper dbHelper;

    public ProjectDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Create
    public long insert(Project project) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PROJECT_NAME, project.getName());
        values.put(DatabaseHelper.COLUMN_PROJECT_DESCRIPTION, project.getDescription());
        values.put(DatabaseHelper.COLUMN_PROJECT_START_DATE, project.getStartDate());
        values.put(DatabaseHelper.COLUMN_PROJECT_END_DATE, project.getEndDate());

        long id = db.insert(DatabaseHelper.TABLE_PROJECTS, null, values);
        project.setId(id);
        return id;
    }

    // Read - Get by ID
    public Project getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PROJECTS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Project project = null;
        if (cursor != null && cursor.moveToFirst()) {
            project = cursorToProject(cursor);
            cursor.close();
        }
        return project;
    }

    // Read - Get All
    public List<Project> getAll() {
        List<Project> projects = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PROJECTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                projects.add(cursorToProject(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return projects;
    }

    // Update
    public int update(Project project) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PROJECT_NAME, project.getName());
        values.put(DatabaseHelper.COLUMN_PROJECT_DESCRIPTION, project.getDescription());
        values.put(DatabaseHelper.COLUMN_PROJECT_START_DATE, project.getStartDate());
        values.put(DatabaseHelper.COLUMN_PROJECT_END_DATE, project.getEndDate());

        return db.update(
                DatabaseHelper.TABLE_PROJECTS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(project.getId())}
        );
    }

    // Delete
    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_PROJECTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Get total budget for a project (sum of all task budgets)
    public double getTotalBudget(long projectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        
        String query = "SELECT SUM(" + DatabaseHelper.COLUMN_TASK_BUDGET + ") as total FROM " +
                DatabaseHelper.TABLE_TASKS + " WHERE " + DatabaseHelper.COLUMN_TASK_PROJECT_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(projectId)});
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    // Get total spent for a project (sum of all expenses in all tasks)
    public double getTotalSpent(long projectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        
        String query = "SELECT SUM(e." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") as total " +
                "FROM " + DatabaseHelper.TABLE_EXPENSES + " e " +
                "INNER JOIN " + DatabaseHelper.TABLE_TASKS + " t ON e." + 
                DatabaseHelper.COLUMN_EXPENSE_TASK_ID + " = t." + DatabaseHelper.COLUMN_ID +
                " WHERE t." + DatabaseHelper.COLUMN_TASK_PROJECT_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(projectId)});
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    // Helper method to convert Cursor to Project object
    private Project cursorToProject(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PROJECT_NAME);
        int descIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PROJECT_DESCRIPTION);
        int startDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PROJECT_START_DATE);
        int endDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PROJECT_END_DATE);

        return new Project(
                cursor.getLong(idIndex),
                cursor.getString(nameIndex),
                cursor.getString(descIndex),
                cursor.getString(startDateIndex),
                cursor.getString(endDateIndex)
        );
    }
}
