package com.codingcrew.memoir.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingcrew.memoir.R;
import com.codingcrew.memoir.models.Task;

import java.util.ArrayList;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {

    ArrayList<Task> tasks;
    OnTasksLongClickListener onTasksLongClickListener;
    OnTaskClickListener onTaskClickListener;
    private Context context;

    public TaskRecyclerAdapter(Context context, OnTasksLongClickListener onTasksLongClickListener, OnTaskClickListener onTaskClickListener) {
        this.context = context;
        this.onTasksLongClickListener = onTasksLongClickListener;
        this.onTaskClickListener = onTaskClickListener;
        tasks = new ArrayList<>();
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task, parent, false), onTaskClickListener, onTasksLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(tasks.get(position).getTitle());
        holder.desc.setText(tasks.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface OnTaskClickListener {
        void onTaskClick(int position);
    }

    public interface OnTasksLongClickListener {
        boolean onTaskLongClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        OnTaskClickListener onTaskClickListener;
        OnTasksLongClickListener onTaskLongClickListener;
        private TextView title, desc;

        public ViewHolder(@NonNull View itemView, OnTaskClickListener onJournalClickListener, OnTasksLongClickListener onJournalLongClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title_item);
            desc = itemView.findViewById(R.id.item_desc);

            this.onTaskClickListener = onJournalClickListener;
            this.onTaskLongClickListener = onJournalLongClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("TAG", "onClick: ");
            onTaskClickListener.onTaskClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return onTaskLongClickListener.onTaskLongClick(getAdapterPosition());
        }
    }
}
