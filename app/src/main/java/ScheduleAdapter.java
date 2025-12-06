package com.example.uiuctempapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    // Listener for both tap (edit) and long-press (delete)
    public interface OnItemInteractionListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    private final ArrayList<ClassItem> classList;
    private final OnItemInteractionListener listener;

    public ScheduleAdapter(ArrayList<ClassItem> classList, OnItemInteractionListener listener) {
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        ClassItem item = classList.get(position);
        holder.title.setText(item.title);
        holder.time.setText(item.time);
        holder.location.setText(item.location);

        // Tap → edit
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

        // Long press → delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(holder.getAdapterPosition());
            }
            return true; // consume the long-press
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, time, location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.classTitle);
            time = itemView.findViewById(R.id.classTime);
            location = itemView.findViewById(R.id.classLocation);
        }
    }
}
