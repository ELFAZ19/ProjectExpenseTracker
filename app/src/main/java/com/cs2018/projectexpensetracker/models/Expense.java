package com.cs2018.projectexpensetracker.models;

public class Expense {
    private long id;
    private long taskId;
    private double amount;
    private String description;
    private String date;

    // Constructors
    public Expense() {
    }

    public Expense(long taskId, double amount, String description, String date) {
        this.taskId = taskId;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Expense(long id, long taskId, double amount, String description, String date) {
        this.id = id;
        this.taskId = taskId;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
