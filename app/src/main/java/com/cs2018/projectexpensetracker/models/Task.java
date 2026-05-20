package com.cs2018.projectexpensetracker.models;

public class Task {
    private long id;
    private long projectId;
    private String name;
    private double budget;
    private String status; // "PENDING" or "COMPLETED"

    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";

    // Constructors
    public Task() {
        this.status = STATUS_PENDING; // Default status
    }

    public Task(long projectId, String name, double budget, String status) {
        this.projectId = projectId;
        this.name = name;
        this.budget = budget;
        this.status = status;
    }

    public Task(long id, long projectId, String name, double budget, String status) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.budget = budget;
        this.status = status;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", name='" + name + '\'' +
                ", budget=" + budget +
                ", status='" + status + '\'' +
                '}';
    }
}
