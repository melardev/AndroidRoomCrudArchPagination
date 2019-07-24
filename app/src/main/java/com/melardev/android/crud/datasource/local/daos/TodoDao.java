package com.melardev.android.crud.datasource.local.daos;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.melardev.android.crud.datasource.local.entities.Todo;

import java.util.List;

@Dao
public interface TodoDao {
    @Insert
    Long insert(Todo todo);

    @Query("SELECT * FROM todo ORDER BY created_at desc")
    LiveData<List<Todo>> getAllAsLiveData();

    @Query("SELECT * FROM Todo WHERE id =:id")
    LiveData<Todo> getByIdAsLiveData(long id);

    @Query("SELECT * FROM Todo WHERE id =:id")
    Todo getById(long id);

    @Update
    void updateTask(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("SELECT * FROM todo ORDER BY created_at desc")
    DataSource.Factory<Integer, Todo> getAllPagedAsLiveData();

    @Query("Select count(*) from todo")
    Long count();

    @Query("Select count(*) from todo")
    LiveData<Long> countAsLiveData();
}
