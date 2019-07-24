package com.melardev.android.crud.todos.write;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.melardev.android.crud.R;
import com.melardev.android.crud.datasource.local.TodoRepository;
import com.melardev.android.crud.datasource.local.entities.Todo;
import com.melardev.android.crud.todos.base.BaseActivity;

public class TodoCreateEditActivity extends BaseActivity {

    private boolean editMode;
    private long todoId;
    private TodoRepository todoRepository;
    private Button btnSaveTodo;

    private TextView txtId;
    private EditText eTxtTitle;
    private EditText eTxtDescription;
    private LiveData<Todo> todoLiveData;
    private Todo todo;
    private CheckBox eCheckboxCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_create_edit);

        todoRepository = TodoRepository.getInstance(getApplicationContext());

        txtId = findViewById(R.id.txtId);
        eTxtTitle = findViewById(R.id.eTxtTitle);
        eTxtDescription = findViewById(R.id.eTxtDescription);
        btnSaveTodo = findViewById(R.id.btnSaveTodo);
        eCheckboxCompleted = findViewById(R.id.eCheckboxCompleted);

        Intent intent = getIntent();
        if (intent != null) {
            this.todoId = intent.getLongExtra("TODO_ID", -1);
            this.editMode = todoId != -1;
        }

        if (editMode) {
            btnSaveTodo.setText("Save Changes");
            this.todoLiveData = this.todoRepository.getByIdAsLiveData(todoId);
            this.todoLiveData.observe(this, todo -> {
                this.todo = todo;
                eTxtTitle.setText(todo.getTitle());
                eTxtDescription.setText(todo.getDescription());
                txtId.setText(String.valueOf(todo.getId()));
                eCheckboxCompleted.setChecked(todo.isCompleted());
            });

        } else {
            btnSaveTodo.setText("Create");
        }
    }

    public void saveTodo(View view) {
        String title = eTxtTitle.getText().toString();
        String description = eTxtDescription.getText().toString();
        boolean completed = eCheckboxCompleted.isChecked();

        if (editMode) {
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setCompleted(completed);
            todoRepository.update(todo, todo -> {
                Toast
                        .makeText(TodoCreateEditActivity.this, "Todo Updated!", Toast.LENGTH_LONG)
                        .show();
                finish();
            });
        } else {
            todoRepository.create(title, description, completed, todo -> {
                Toast
                        .makeText(TodoCreateEditActivity.this, "Todo Created!", Toast.LENGTH_LONG)
                        .show();

                getIntent().putExtra("TODO", todo);
                finish();
            });
        }

    }

    public void deleteTodo(View view) {
        if (editMode) {
            this.todoRepository.delete(this.todoLiveData.getValue(), success -> {
                if (success) {
                    Toast.makeText(this, "Todo Deleted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error Deleting todos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
