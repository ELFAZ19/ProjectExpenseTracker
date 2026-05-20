package com.cs2018.projectexpensetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "ProjectExpenseManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PROJECTS = "projects";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_EXPENSES = "expenses";

    // Common Column Names
    public static final String COLUMN_ID = "id";

    // Projects Table Columns
    public static final String COLUMN_PROJECT_NAME = "name";
    public static final String COLUMN_PROJECT_DESCRIPTION = "description";
    public static final String COLUMN_PROJECT_START_DATE = "start_date";
    public static final String COLUMN_PROJECT_END_DATE = "end_date";

    // Tasks Table Columns
    public static final String COLUMN_TASK_PROJECT_ID = "project_id";
    public static final String COLUMN_TASK_NAME = "name";
    public static final String COLUMN_TASK_BUDGET = "budget";
    public static final String COLUMN_TASK_STATUS = "status";

    // Expenses Table Columns
    public static final String COLUMN_EXPENSE_TASK_ID = "task_id";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_DESCRIPTION = "description";
    public static final String COLUMN_EXPENSE_DATE = "date";

    // Create Projects Table
    private static final String CREATE_TABLE_PROJECTS =
            "CREATE TABLE " + TABLE_PROJECTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROJECT_NAME + " TEXT NOT NULL, " +
                    COLUMN_PROJECT_DESCRIPTION + " TEXT, " +
                    COLUMN_PROJECT_START_DATE + " TEXT, " +
                    COLUMN_PROJECT_END_DATE + " TEXT" +
                    ")";

    // Create Tasks Table
    private static final String CREATE_TABLE_TASKS =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TASK_PROJECT_ID + " INTEGER NOT NULL, " +
                    COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                    COLUMN_TASK_BUDGET + " REAL NOT NULL DEFAULT 0, " +
                    COLUMN_TASK_STATUS + " TEXT NOT NULL DEFAULT 'PENDING', " +
                    "FOREIGN KEY(" + COLUMN_TASK_PROJECT_ID + ") REFERENCES " +
                    TABLE_PROJECTS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";

    // Create Expenses Table
    private static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE " + TABLE_EXPENSES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXPENSE_TASK_ID + " INTEGER NOT NULL, " +
                    COLUMN_EXPENSE_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_EXPENSE_DESCRIPTION + " TEXT, " +
                    COLUMN_EXPENSE_DATE + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_EXPENSE_TASK_ID + ") REFERENCES " +
                    TABLE_TASKS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";

    // Singleton instance
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON");
        
        // Create tables
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key constraints
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    // Method to clear all data
    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EXPENSES);
        db.execSQL("DELETE FROM " + TABLE_TASKS);
        db.execSQL("DELETE FROM " + TABLE_PROJECTS);
    }
}
