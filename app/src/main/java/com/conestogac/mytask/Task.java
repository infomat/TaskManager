package com.conestogac.mytask;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Timer;

/**
 * Created by infomat on 16-06-09.
 */
public class Task implements Parcelable {
    private Integer _id;
    private String todo;
    private Integer priority;
    private Integer isCompleted;
    private String dueDateTime;

    //Default Constructor
    public Task() {
    }

    //Set Constructor
    public Task(Integer _id, String todo, Integer priority, String dueDateTime, Integer isCompleted) {
        this._id = _id;
        this.todo = todo;
        this.priority = priority;
        this.dueDateTime = dueDateTime;
        this.isCompleted = isCompleted;
    }

    //Define setter and getter
    //This is good practice for information hiding for modularity
    public void setId(Integer _id) { this._id = _id; }
    public Integer getId() {
        return this._id;
    }

    public void setTodo(String todo) { this.todo = todo; }
    public String getTodo() {
        return this.todo;
    }

    public void setPriority(Integer priority) { this.priority = priority; }
    public Integer getPriority() { return this.priority; }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }
    public String getDueDateTime() {
        return this.dueDateTime;
    }

    public void setIsCompleted(Integer isCompleted) {
        this.isCompleted = isCompleted;
    }
    public Integer getIsIsCompleted() {
        return this.isCompleted;
    }

    /**
     *
     * Implement Parcelable Interface, to send task object between object
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public Task(Parcel in){
        String[] data= new String[4];

        in.readStringArray(data);
        this._id = Integer.parseInt(data[0]);
        this.todo = data[1];
        this.priority = Integer.parseInt(data[2]);
        this.dueDateTime = data[3];
        //isCompleted is not plan to send
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this._id),this.todo,
                                String.valueOf(this.priority),this.dueDateTime});
    }

    public static final Parcelable.Creator<Task> CREATOR= new Parcelable.Creator<Task>() {

        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);  //using parcelable constructor
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
