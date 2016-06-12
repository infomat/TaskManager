package com.conestogac.mytask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to use database
 */

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final String TASK_TABLE_NAME = "tasks";
    private static final int SCHEMA_VERSION = 1;
    public static final String KEY_TASKS_ID = "_id";
    public static final String KEY_TASKS_TODO = "todo";
    public static final String KEY_TASKS_PRIORITY = "priority";
    public static final String KEY_TASKS_DUEDATE = "duedate";
    public static final String KEY_TASKS_ISCOMPLETED = "iscompleted";  //Todo this is not needed


    private static final String TAG = TaskDatabaseHelper.class.getSimpleName();

    private static TaskDatabaseHelper singleton = null;

    //Create database helper object as singleton: By creating only one object
    //to ensure that all threads are accessing the same SQLiteDatabase
    //Object
    synchronized static TaskDatabaseHelper getInstance(Context ctxt) {
        if (singleton == null) {
            singleton = new TaskDatabaseHelper(ctxt.getApplicationContext());
        }
        return (singleton);
    }

    //constructor of helper class -> call super class(SQLiteOpenHelper)
    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    //Create table with id autoincrement
    //Check name null and set mark default 0, to prevent garbage data into db
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TASK_TABLE_NAME  +
                "("+
                KEY_TASKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TASKS_TODO + " TEXT NOT NULL, " +
                KEY_TASKS_PRIORITY + " INTEGER DEFAULT 0, " +
                KEY_TASKS_DUEDATE + " TEXT, " +
                KEY_TASKS_ISCOMPLETED + " INTEGER DEFAULT 0);");
    }



    //Insert task
    public boolean insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASKS_TODO, task.getTodo());
        cv.put(KEY_TASKS_PRIORITY, task.getPriority());
        cv.put(KEY_TASKS_DUEDATE, task.getDueDateTime());
        cv.put(KEY_TASKS_ISCOMPLETED, task.getIsIsCompleted());
        db.insert(TASK_TABLE_NAME, null, cv);

        db.close();
        return true;
    }

    //Get data using ID
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from grades where _id=" + id + "", null);
        //Close cursor(), to send sure
        res.close();
        return res;
    }

    //Get number of rows in the table
    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TASK_TABLE_NAME);
        return numRows;
    }

    //Update record with given ID
    public Integer updateTask(Task task) {
        Integer numberOfUpdated;

        Log.d(TAG,"_ID= "+task.getId()+"Todo "+task.getTodo()+"Priority "+task.getPriority()
                +"DueDate= "+task.getDueDateTime()+" Completed= "+task.getIsIsCompleted());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //to avoid update empty task
        //isEmpty should be used for string comparision
        if (task.getTodo().isEmpty() == false) {
            cv.put(KEY_TASKS_TODO, task.getTodo());
        }

        //priority will be updated if priority is entered. If it is -1 means,
        // it is empty which is set at Main activity
        if (task.getPriority() != -1) {
            cv.put(KEY_TASKS_PRIORITY, task.getPriority());
        }

        //to avoid update empty datetime
        //isEmpty should be used for string comparision
        if (task.getDueDateTime().isEmpty() == false)
        {
            cv.put(KEY_TASKS_DUEDATE, task.getDueDateTime());
        }


        //update data by search with ID
        //UPDATE tasks SET task=a, duedate=b, iscompleted=c WHERE ID=id
        numberOfUpdated = db.update(TASK_TABLE_NAME, cv, "_id = ? ",
                                    new String[]{Integer.toString(task.getId())});

        //close database to ensure database will be closed state in exceptional case
        db.close();
        return numberOfUpdated;
    }

    //Delete Grade in the table with ID
    public Integer deleteTask(Integer id) {

        Integer ret_value;
        SQLiteDatabase db = this.getWritableDatabase();

        //delete ID
        ret_value = db.delete(TASK_TABLE_NAME,
                "_id = ? ",
                new String[]{Integer.toString(id)});

        db.close();
        return ret_value;
    }


    //Get all list
    public Cursor getAllTasks() {
        //Get Database object as Readable
        SQLiteDatabase db = this.getReadableDatabase();
        //Get cursor
        Cursor res = db.rawQuery("select * from "+ TASK_TABLE_NAME, null);
        //Move cursor to point first
        res.moveToFirst();

        return res;
    }

    //process exception in case of upgrade of database which version will be managed by SCHEMA_VERSION
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("Upgrade is not being supported");
    }

}
