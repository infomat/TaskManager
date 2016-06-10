package com.conestogac.mytask;


import java.util.Date;
import java.util.Timer;

/**
 * Created by infomat on 16-06-09.
 */
public class Task {
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

}
