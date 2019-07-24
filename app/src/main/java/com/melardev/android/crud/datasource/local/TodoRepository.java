package com.melardev.android.crud.datasource.local;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Room;

import com.melardev.android.crud.datasource.local.daos.TodoDao;
import com.melardev.android.crud.datasource.local.databases.AppDatabase;
import com.melardev.android.crud.datasource.local.entities.Todo;
import com.melardev.android.crud.utils.DateUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class TodoRepository {

    private static TodoRepository instance;
    private static final Object mutex = new Object();
    private AppDatabase appDatabase;

    private TodoRepository(Context context) {
        String DB_NAME = "todos.db";
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    // The end goal is to have a unique Dao, that is used everywhere, because if you use different
    // instance then you will not get notified upon changes, example:
    // If MainActivity is using its own Dao instance, and TodoCreateOrEditActivity is using a different
    // instance, then once you create a new To do the MainActivity won't get notified
    // so we need a single instance of Dao, which may be achieved creating the AppDataBase once
    // which I achieved implementing TodoRepository as a Singleton
    public static TodoRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null) {
                    instance = new TodoRepository(context);
                }
            }
        }

        return instance;
    }

    public LiveData<List<Todo>> getAll() {
        return appDatabase.todoDao().getAllAsLiveData();
    }

    public void create(String title,
                       String description,
                       TodoListener listener) {
        create(title, description, false, listener);
    }

    public void create(String title,
                       String description, boolean completed, TodoListener listener) {

        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(completed);
        todo.setCreatedAt(DateUtils.getCurrentDate());
        todo.setUpdatedAt(DateUtils.getCurrentDate());

        create(todo, listener);
    }

    public void create(final Todo todo, TodoListener listener) {
        new CreateTodoAsync(appDatabase.todoDao(), todo, listener).execute();
    }

    public void update(final Todo todo, TodoListener listener) {
        todo.setUpdatedAt(DateUtils.getCurrentDate());
        new UpdateTodoAsync(appDatabase.todoDao(), todo, listener).execute();
    }

    public void delete(final long id, DatabaseResultListener listener) {
        new DeleteTodoAsyncTask(id, listener, appDatabase.todoDao()).execute();
    }

    private Todo getById(long id) {
        return appDatabase.todoDao().getById(id);
    }

    public void delete(final Todo todo, DatabaseResultListener listener) {
        if (todo != null) {
            new DeleteTodoAsyncTask(todo, listener, appDatabase.todoDao()).execute();
        }
    }

    public LiveData<Todo> getByIdAsLiveData(long id) {
        return appDatabase.todoDao().getByIdAsLiveData(id);
    }

    public DataSource.Factory<Integer, Todo> getAllPaged() {
        return appDatabase.todoDao().getAllPagedAsLiveData();
    }

    public void getCount(CountResultListener listener) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (listener != null)
                    listener.onResult(appDatabase.todoDao().count());

                return null;
            }
        }.execute();
    }

    public interface DatabaseResultListener {
        void onResult(boolean success);
    }

    public interface CountResultListener {
        void onResult(Long count);
    }

    public interface TodoListener {
        void onTodo(Todo todo);
    }


    public static abstract class SingleTodoAsync extends AsyncTask<Void, Void, Void> {

        protected final TodoDao todoDao;
        protected final Todo todo;
        protected final WeakReference<TodoListener> weakListener;

        public SingleTodoAsync(TodoDao todoDao, Todo todo, TodoListener weakListener) {
            this.todoDao = todoDao;
            this.todo = todo;
            this.weakListener = new WeakReference<>(weakListener);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TodoListener listener = weakListener.get();
            if (listener == null)
                return;

            listener.onTodo(todo);
        }
    }

    public static class UpdateTodoAsync extends SingleTodoAsync {

        public UpdateTodoAsync(TodoDao todoDao, Todo todo, TodoListener listener) {
            super(todoDao, todo, listener);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todoDao.updateTask(todo);
            return null;
        }

    }

    public static class CreateTodoAsync extends SingleTodoAsync {

        public CreateTodoAsync(TodoDao todoDao, Todo todo, TodoListener listener) {
            super(todoDao, todo, listener);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todo.setId(todoDao.insert(todo));
            return null;
        }
    }

    public static class DeleteTodoAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<DatabaseResultListener> weakListener;
        private final TodoDao dao;
        private Todo todo;
        private Long id;

        public DeleteTodoAsyncTask(Todo todo, DatabaseResultListener listener, TodoDao dao) {
            this.todo = todo;
            weakListener = new WeakReference<>(listener);
            this.dao = dao;
        }

        public DeleteTodoAsyncTask(long id, DatabaseResultListener listener, TodoDao dao) {
            this.id = id;
            this.weakListener = new WeakReference<>(listener);
            this.dao = dao;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (todo == null) {
                if (id == null) return false;

                todo = dao.getById(id);
            }
            dao.delete(todo);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            DatabaseResultListener listener = weakListener.get();
            if (listener == null) {
                return;
            }
            listener.onResult(result);
        }
    }
}
