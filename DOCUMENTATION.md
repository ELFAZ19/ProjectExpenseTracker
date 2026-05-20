# CostNest - Documentation

This document provides a detailed overview of the project structure, classes, and key components of the CostNest application.

## File Structure

```
app/src/main/java/com/cs2018/projectexpensetracker/
├── activities/           # Activity classes (UI Controllers)
├── adapters/             # RecyclerView Adapters
├── database/             # Database Helper and DAOs
├── models/               # Data Transfer Objects (POJOs)
├── utils/                # Utility classes
├── MainActivity.java     # Splash Screen and Entry Point
└── ProjectExpenseApplication.java # Application Initialization
```

## Class Descriptions

### Activities (`com.cs2018.projectexpensetracker.activities`)

-   **ProjectListActivity**: The main dashboard displaying the list of all projects.
-   **AddEditProjectActivity**: Form to create a new project or edit an existing one.
-   **TaskListActivity**: Displays tasks associated with a specific project.
-   **AddEditTaskActivity**: Form to add or edit tasks within a project.
-   **ExpenseListActivity**: Lists expenses for a specific task.
-   **AddEditExpenseActivity**: Form to log new expenses.
-   **ProjectSummaryActivity**: Visual summary of a project's budget, including charts and statistics. Also allows exporting the project report to a CSV file.
-   **SettingsActivity**: Application settings, including Theme selection and Data Management (Clear Data).

### Database (`com.cs2018.projectexpensetracker.database`)

-   **DatabaseHelper**: Singleton class managing the SQLite database creation, upgrades, and providing checking methods.
-   **ProjectDAO**: Data Access Object for determining Project-related database operations.
-   **TaskDAO**: DAO for Task-related operations.
-   **ExpenseDAO**: DAO for Expense-related operations.

### Context (`com.cs2018.projectexpensetracker.models`)

-   **Project**: Represents a project entity with name, description, dates, budget, etc.
-   **Task**: Represents a task entity linked to a project.
-   **Expense**: Represents an expense entity linked to a task.

### Adapters (`com.cs2018.projectexpensetracker.adapters`)

-   **ProjectAdapter**: Binds Project data to the RecyclerView in ProjectListActivity.
-   **TaskAdapter**: Binds Task data to the RecyclerView in TaskListActivity.
-   **ExpenseAdapter**: Binds Expense data to the RecyclerView in ExpenseListActivity.

### Utilities (`com.cs2018.projectexpensetracker.utils`)

-   **ThemeHelper**: Manages the application theme (Light, Dark, System) using SharedPreferences and AppCompatDelegate.

### Core

-   **MainActivity**: Acts as the Splash Screen, displaying the logo and app name with animations before transitioning to ProjectListActivity.
-   **ProjectExpenseApplication**: Evaluation class that initializes global configurations (like Theme) on app startup.

## Resources (`res`)

-   **layout/**: XML files defining the UI structure for activities and list items.
-   **values/**: Contains strings, colors, dimensions, and themes (Light mode).
-   **values-night/**: Contains theme overrides for Dark mode.
-   **drawable/**: Icons and background drawables.

## Database Schema

The application uses a relational SQLite database with the following tables:
1.  **projects**: Stores project details.
2.  **tasks**: Stores tasks, linked to projects via foreign key.
3.  **expenses**: Stores expenses, linked to tasks via foreign key.
