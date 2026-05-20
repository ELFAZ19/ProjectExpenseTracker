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
import com.cs2018.projectexpensetracker.database.TaskDAO;
import com.cs2018.projectexpensetracker.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private List<Task> tasks;
    private TaskDAO taskDAO;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskLongClick(Task task);
    }

    public TaskAdapter(Context context, List<Task> tasks, OnTaskClickListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
        this.taskDAO = new TaskDAO(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        
        // Set task name
        holder.tvName.setText(task.getName());
        
        // Set status badge
        if (task.isCompleted()) {
            holder.tvStatus.setText(context.getString(R.string.task_completed));
            holder.tvStatus.setBackgroundResource(R.drawable.badge_completed);
        } else {
            holder.tvStatus.setText(context.getString(R.string.task_pending));
            holder.tvStatus.setBackgroundResource(R.drawable.badge_pending);
        }
        
        // Get total spent from database
        double totalSpent = taskDAO.getTotalSpent(task.getId());
        
        // Set budget and spent
        holder.tvBudget.setText(String.format("Br%,.2f", task.getBudget()));
        holder.tvSpent.setText(String.format("Br%,.2f", totalSpent));
        
        // Calculate progress
        int progress = 0;
        if (task.getBudget() > 0) {
            progress = (int) ((totalSpent / task.getBudget()) * 100);
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
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTaskLongClick(task);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvBudget, tvSpent;
        ProgressBar progressBar;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_task_name);
            tvStatus = itemView.findViewById(R.id.tv_status_badge);
            tvBudget = itemView.findViewById(R.id.tv_task_budget);
            tvSpent = itemView.findViewById(R.id.tv_task_spent);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
