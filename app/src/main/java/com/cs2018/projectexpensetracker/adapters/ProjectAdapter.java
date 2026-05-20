package com.cs2018.projectexpensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.database.ProjectDAO;
import com.cs2018.projectexpensetracker.models.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private Context context;
    private List<Project> projects;
    private ProjectDAO projectDAO;
    private OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
        void onProjectLongClick(Project project);
    }

    public ProjectAdapter(Context context, List<Project> projects, OnProjectClickListener listener) {
        this.context = context;
        this.projects = projects;
        this.listener = listener;
        this.projectDAO = new ProjectDAO(context);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        
        // Set project name and description
        holder.tvName.setText(project.getName());
        holder.tvDescription.setText(project.getDescription() != null && !project.getDescription().isEmpty()
                ? project.getDescription()
                : "No description");
        
        // Set dates
        String dates = formatDateRange(project.getStartDate(), project.getEndDate());
        holder.tvDates.setText(dates);
        
        // Get budget and spent from database
        double totalBudget = projectDAO.getTotalBudget(project.getId());
        double totalSpent = projectDAO.getTotalSpent(project.getId());
        
        // Set budget and spent
        holder.tvBudget.setText(String.format("Br%,.2f", totalBudget));
        holder.tvSpent.setText(String.format("Br%,.2f", totalSpent));
        
        // Calculate progress
        int progress = 0;
        if (totalBudget > 0) {
            progress = (int) ((totalSpent / totalBudget) * 100);
        }
        
        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(progress);
        
        // Set progress color based on percentage
        int progressColor;
        if (progress < 80) {
            progressColor = ContextCompat.getColor(context, R.color.budget_good);
        } else if (progress <= 100) {
            progressColor = ContextCompat.getColor(context, R.color.budget_warning);
        } else {
            progressColor = ContextCompat.getColor(context, R.color.budget_danger);
        }
        holder.progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(progressColor));
        
        // Set progress text
        holder.tvProgressPercentage.setText(String.format("%d%% of budget used", progress));
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProjectClick(project);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onProjectLongClick(project);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void updateProjects(List<Project> newProjects) {
        this.projects = newProjects;
        notifyDataSetChanged();
    }

    private String formatDateRange(String startDate, String endDate) {
        if (startDate == null && endDate == null) {
            return "No dates set";
        } else if (startDate != null && endDate != null) {
            return startDate + " - " + endDate;
        } else if (startDate != null) {
            return "From " + startDate;
        } else {
            return "Until " + endDate;
        }
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvDates, tvBudget, tvSpent, tvProgressPercentage;
        ProgressBar progressBar;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_project_name);
            tvDescription = itemView.findViewById(R.id.tv_project_description);
            tvDates = itemView.findViewById(R.id.tv_project_dates);
            tvBudget = itemView.findViewById(R.id.tv_budget);
            tvSpent = itemView.findViewById(R.id.tv_spent);
            tvProgressPercentage = itemView.findViewById(R.id.tv_progress_percentage);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
