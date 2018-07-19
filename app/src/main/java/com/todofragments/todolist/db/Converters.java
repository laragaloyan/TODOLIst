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
        public List<TodoItem.Repeat> storedStringToEnum(String value) {
                List<String> dbValues = Arrays.asList(value.split("\\S"));
                List<TodoItem.Repeat> enums = new ArrayList<>();

                for (String string: dbValues)
                        enums.add(TodoItem.Repeat.valueOf(string));

                return enums;
        }

        @TypeConverter
        public String enumsToStoredString(List<TodoItem.Repeat> repeats) {
                StringBuilder value = new StringBuilder();

                for (TodoItem.Repeat repeat : repeats)
                        value.append(repeat.name()).append(",");

                return value.toString();
        }
}
