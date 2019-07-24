package com.melardev.android.crud;

import android.app.Application;

import com.github.javafaker.Faker;
import com.melardev.android.crud.datasource.local.TodoRepository;
import com.melardev.android.crud.datasource.local.entities.Todo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CrudApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TodoRepository todoRepository = TodoRepository.getInstance(this);
        todoRepository.getCount(count -> {
            Faker faker = new Faker(new Random(System.currentTimeMillis()));


            while (count < 20) {
                Date startDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startDate = Date.from(LocalDateTime.of(2016, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(2016, 1, 1);
                    startDate = calendar.getTime();
                }

                Date endDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    endDate = Date.from(LocalDateTime.of(2019, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(2019, 1, 1);
                    endDate = calendar.getTime();
                }

                String title = faker.lorem().words(faker.random().nextInt(2, 5)).toString().replace(", ", ",").replaceAll("[\\[.\\]]", "");
                String description = faker.lorem()
                        .sentences(faker.random().nextInt(1, 3)).toString().replace(", ", ",").replaceAll("[\\[.\\]]", "");
                Todo todo = new Todo(title, description, faker.random().nextBoolean());

                Date dateForCreatedAt = faker.date().between(startDate, endDate);

                Date dateForUpdatedAt = faker.date().future(2 * 365, TimeUnit.DAYS, dateForCreatedAt);

                todo.setCreatedAt(dateForCreatedAt);
                todo.setUpdatedAt(dateForUpdatedAt);
                todoRepository.create(todo, null);
                count++;
            }

        });
    }
}
