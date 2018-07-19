package com.todofragments.todolist.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.todofragments.todolist.R;
import com.todofragments.todolist.TodoItem;
//import com.todofragments.todolist.db.DatabaseManager;
import com.todofragments.todolist.db.TodoItemDB;
//import com.todofragments.todolist.db.Todo_Item_Room;
import com.todofragments.todolist.fragments.TodoItemFragment;
import com.todofragments.todolist.fragments.TodoItemsListFragment;

public class MainActivity extends AppCompatActivity {

    TodoItemDB db = Room.databaseBuilder(getApplicationContext(),
            TodoItemDB.class, "database-name").build();

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_activity_main:
                    openAddTodoItem();
                    break;
            }
        }
    };

    private TodoItemsListFragment.OnFragmentInteractionListener mOnTodoItemsInteractionListener =
            new TodoItemsListFragment.OnFragmentInteractionListener() {
                @Override
                public void onEditItem(TodoItem todoItem) {
                    db.TodoDao().insertAll(todoItem);
                    openEditTodoItem(todoItem);
                }
            };

    private TodoItemFragment.OnFragmentInteractionListener mOnTodoItemInteractionListener =
            new TodoItemFragment.OnFragmentInteractionListener() {
                @Override
                public void onItemCreated(TodoItem todoItem) {
                    delegateItemCreationToFragment(todoItem);
                }

                @Override
                public void onItemChanged(TodoItem todoItem) {
                    db.TodoDao().update(todoItem);
                    delegateItemChangeToFragment(todoItem);
                }
            };

    private TodoItemsListFragment mTodoItemsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.opitons_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_fragment_todo_item_edit){
            Toast.makeText(MainActivity.this, "You've clicked", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.action_view_todo_item_remove){
            Toast.makeText(MainActivity.this, "You've clicked, Yay!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        FloatingActionButton fab = findViewById(R.id.fab_activity_main);
        fab.setOnClickListener(mOnClickListener);

        mTodoItemsListFragment = (TodoItemsListFragment) (getFragmentManager().findFragmentById(R.id.fragment_activity_main));
        mTodoItemsListFragment.setOnInteractionListener(mOnTodoItemsInteractionListener);
    }

    private void openAddTodoItem() {
        TodoItemFragment todoItemFragment = TodoItemFragment.newInstance(null);
        todoItemFragment.setOnInteractionListener(mOnTodoItemInteractionListener);
        openFragmentInContainer(todoItemFragment, true);
    }

    private void openEditTodoItem(TodoItem todoItem) {
        TodoItemFragment todoItemFragment = TodoItemFragment.newInstance(todoItem);
        todoItemFragment.setOnInteractionListener(mOnTodoItemInteractionListener);
        openFragmentInContainer(todoItemFragment, true);
    }

    private void delegateItemCreationToFragment(TodoItem todoItem) {
        //insert
        mTodoItemsListFragment.addTodoItem(todoItem);
        getFragmentManager().popBackStack();
    }

    private void delegateItemChangeToFragment(TodoItem todoItem) {
        mTodoItemsListFragment.editTodoItem(todoItem);
        getFragmentManager().popBackStack();
    }

    private void openFragmentInContainer(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.container_activity_main, fragment);
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        fragmentTransaction.commit();
    }
}
