package com.todofragments.todolist.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.todofragments.todolist.TodoItem;


@Database(entities = {TodoItem.class}, version = 1)
@TypeConverters({Converters.class})
    public abstract class TodoItemDB extends RoomDatabase {
        public abstract Todo_Dao TodoDao();


    }

