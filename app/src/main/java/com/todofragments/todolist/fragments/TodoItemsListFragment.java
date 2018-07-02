package com.todofragments.todolist.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.todofragments.todolist.R;
import com.todofragments.todolist.TodoItem;
import com.todofragments.todolist.TodoItemAdapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class TodoItemsListFragment extends android.app.Fragment {

    private OnFragmentInteractionListener mListener;

    public TodoItemsListFragment() {
        // Required empty public constructor
    }




    public static TodoItemsListFragment newInstance() {
        TodoItemsListFragment fragment = new TodoItemsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private TodoItemAdapter.OnItemSelectedListener mOnItemSelectedListener = new TodoItemAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(TodoItem todoItem) {
            if (mListener != null) {
                mListener.onEditItem(todoItem);
            }
        }
    };

    private TodoItemAdapter mTodoItemAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View root) {
        mTodoItemAdapter = new TodoItemAdapter();
        mTodoItemAdapter.setOnItemSelectedListener(mOnItemSelectedListener);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_fragment_todo_items_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mTodoItemAdapter);
    }

    public void addTodoItem(TodoItem todoItem) {
        mTodoItemAdapter.addItem(todoItem);
    }

    public void editTodoItem(TodoItem todoItem) {
        mTodoItemAdapter.updateItem(todoItem);
    }

    public void setOnInteractionListener(OnFragmentInteractionListener onInteractionListener) {
        mListener = onInteractionListener;
    }


    public interface OnFragmentInteractionListener {
        void onEditItem(TodoItem todoItem);
    }
}
