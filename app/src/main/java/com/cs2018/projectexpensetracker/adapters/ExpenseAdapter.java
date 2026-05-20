package com.cs2018.projectexpensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs2018.projectexpensetracker.R;
import com.cs2018.projectexpensetracker.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private Context context;
    private List<Expense> expenses;
    private OnExpenseClickListener listener;

    public interface OnExpenseClickListener {
        void onExpenseLongClick(Expense expense);
    }

    public ExpenseAdapter(Context context, List<Expense> expenses, OnExpenseClickListener listener) {
        this.context = context;
        this.expenses = expenses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        
        // Set amount
        holder.tvAmount.setText(String.format("Br%,.2f", expense.getAmount()));
        
        // Set date
        holder.tvDate.setText(expense.getDate() != null ? expense.getDate() : "No date");
        
        // Set description
        holder.tvDescription.setText(expense.getDescription() != null && !expense.getDescription().isEmpty()
                ? expense.getDescription()
                : "No description");
        
        // Long click listener for edit/delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onExpenseLongClick(expense);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDate, tvDescription;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_expense_amount);
            tvDate = itemView.findViewById(R.id.tv_expense_date);
            tvDescription = itemView.findViewById(R.id.tv_expense_description);
        }
    }
}
