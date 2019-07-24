package com.melardev.android.crud.datasource.local.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.melardev.android.crud.datasource.local.daos.TodoDao;
import com.melardev.android.crud.datasource.local.entities.Todo;

@Database(entities = {Todo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TodoDao todoDao();

}
