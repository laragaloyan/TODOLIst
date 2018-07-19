package com.todofragments.todolist.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.todofragments.todolist.TodoItem;

import java.util.Date;
import java.util.List;

@Dao
public interface Todo_Dao {

    @Query("SELECT * FROM TodoItem")
    List<TodoItem> getAll();

    @Query("SELECT * FROM TodoItem WHERE mId IN (:userIds)")
    List<TodoItem> loadAllByIds(int[] userIds);


    @Insert
    void insertAll(TodoItem... todoitems);

    @Delete
    void delete(TodoItem todoitems);

    @Update
    void  update (TodoItem totoitems);


}
