package com.todofragments.todolist.db;

import android.arch.persistence.room.TypeConverter;

import com.todofragments.todolist.TodoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Converters {
        @TypeConverter
        public static Date fromTimestamp(Long value) {
                return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
                return date == null ? null : date.getTime();
        }

        @TypeConverter
        public TodoItem.Repeat storedStringToEnum(String value) {

                return TodoItem.Repeat.valueOf(value);
        }

        @TypeConverter
        public String enumsToStoredString(TodoItem.Repeat repeat) {
                return repeat.name();
        }
}
