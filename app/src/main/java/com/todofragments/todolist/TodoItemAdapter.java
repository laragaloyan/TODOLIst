package com.todofragments.todolist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.todofragments.todolist.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TodoItemAdapter extends RecyclerView.Adapter<TodoItemViewHolder> {
    private TodoItemViewHolder.OnItemClickListener mOnItemClickListener =
            new TodoItemViewHolder.OnItemClickListener() {
                @Override
                public void onItemClick(int adapterPosition) {
                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onItemSelected(mData.get(adapterPosition));
                    }
                }

                @Override
                public void onRemoveClick(int adapterPosition) {
                    removeItem(adapterPosition);
                }
            };

    private OnItemSelectedListener mOnItemSelectedListener;

    private List<TodoItem> mData;

    public TodoItemAdapter() {
        mData = new ArrayList<>();
    }

    @NonNull
    @Override
    public TodoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TodoItemViewHolder viewHolder = new TodoItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_todo_item, parent, false));
        viewHolder.setOnItemClickListener(mOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodoItemViewHolder holder, int position) {
        TodoItem item = mData.get(position);
        holder.getTitle().setText(item.getTitle());
        if (item.getDescription() != null) {
            holder.getDescription().setText(item.getDescription());
        } else {
            holder.getDescription().setText(item.getDescription());
        }
        holder.getDate().setText(DateUtil.formatDateToLongStyle(item.getDate()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(TodoItem item) {
        mData.add(item);
        notifyItemInserted(mData.size() - 1);
    }

    public void updateItem(TodoItem item) {
        // Find item and update
        // Not the best solution, it is possible update an item by accepting position
        // from outside. For current data structure and 'client' implementation
        // this is best of worst.
        for (int i = 0; i < mData.size(); i++) {
            if (Objects.equals(item, mData.get(i))) {
                mData.set(i, item);
                notifyItemChanged(i);
            }
        }
    }

    private void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(TodoItem todoItem);
    }
}