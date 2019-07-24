package com.melardev.android.crud.datasource.local.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.melardev.android.crud.datasource.local.converters.LocalDateTimeConverter;

import java.util.Date;

@Entity
public class Todo implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String title;

    private String description;

    private boolean completed;

    @ColumnInfo(name = "created_at")
    @TypeConverters({LocalDateTimeConverter.class})
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    @TypeConverters({LocalDateTimeConverter.class})
    private Date updatedAt;


    public Todo() {
    }


    protected Todo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        description = in.readString();
        completed = in.readByte() != 0;
        createdAt = new Date(in.readLong());
        updatedAt = new Date(in.readLong());
    }

    public Todo(String title, String description, Boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeLong(getCreatedAt().getTime());
        dest.writeLong(getUpdatedAt().getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
