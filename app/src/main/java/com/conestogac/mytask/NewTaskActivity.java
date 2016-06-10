package com.conestogac.mytask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This activity is for getting task from user
 */
public class NewTaskActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = NewTaskActivity.class.getSimpleName();
    private static final String[] listPriority={"None", "Low", "Mid", "Important"};
    private static String[] listDate={"Today", "Tomorrow", "Next", "Pick a date..."};
    private static final String[] listTime={"Morning 08:00AM", "Afternoon 1:00PM", "Evening 6:00PM", "Night 8:00PM", "Pick a time..."};
    private SimpleDateFormat sdf_dayOfWeek = new SimpleDateFormat("EEEE");
    private SimpleDateFormat sdf_user = new SimpleDateFormat("EEE MMM dd, hh:mm a");

    private Date today = new Date();
    Task taskItem = new Task();

    private EditText edTodo;
    private Integer mPriority;
    private Calendar mCal;
    private TaskDatabaseHelper mytaskdb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtask);

        //create instance of db
        mytaskdb = new TaskDatabaseHelper(this);

        //set up widget
        edTodo = (EditText) findViewById(R.id.edTodo);
        setupSpinner();

        //get current date, time
        mCal = Calendar.getInstance();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent,
            View v, int position, long id) {

        parent.getItemAtPosition(position);

        switch(parent.getId()) {

            case R.id.spPriority:
                Log.d(TAG,"Priority: "+position);
                afterPrioritySelect(position);
                break;

            case R.id.spDate:
                Log.d(TAG,"Date "+position);
                afterDateSelect(position);
                break;

            case R.id.spTime:
                Log.d(TAG,"Time "+position);
                afterTimeSelect(position);
                break;
        }
        return;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
     //   selection.setText("");
    }

    //Add Button
    public void onAddTask(View view) {
        String strDate;
        Log.d(TAG, "Add Button");
        taskItem.setTodo(edTodo.getText().toString());
        taskItem.setPriority(mPriority);

        strDate = sdf_user.format(mCal.getTime());
        Log.d(TAG, "ADD Date: "+strDate);
        taskItem.setDueDateTime(strDate);

        //pass markitem object to data helper
        if (mytaskdb.insertTask(taskItem)) {
            Toast.makeText(getApplicationContext(), "Task is added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error!!! Please try again.", Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }

    //Complete Button
    public void onCompleteTask(View view) {
        Log.d(TAG, "Complete Button");
    }

    // To setup spinner to get Priority, Data, Time
    private void setupSpinner() {
        //Get Spinner and set listener to get event
        Spinner spPriority=(Spinner)findViewById(R.id.spPriority);
        spPriority.setOnItemSelectedListener(this);

        Spinner spDate=(Spinner)findViewById(R.id.spDate);
        spDate.setOnItemSelectedListener(this);

        Spinner spTime=(Spinner)findViewById(R.id.spTime);
        spTime.setOnItemSelectedListener(this);

        //To set next week
        Log.d(TAG, "Next " + sdf_dayOfWeek.format(today));
        listDate[2] = "Next " + sdf_dayOfWeek.format(today);

        ArrayAdapter<String> aaPriority=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                listPriority);
        ArrayAdapter<String> aaListDate=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                listDate);
        ArrayAdapter<String> aaListTime=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                listTime);

        aaPriority.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(aaPriority);

        aaListDate.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(aaListDate);

        aaListTime.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(aaListTime);
    }


    private void afterPrioritySelect(Integer position) {

        switch(position) {
            case 0: case 1: case 2: case 3:
                mPriority = position;
                break;

            default:
                mPriority = 0;
        }
    }

    private void afterDateSelect(Integer position) {
        switch(position) {
            case 0:
                //Today
                mCal.setTime(today);
                break;
            case 1:
                //Tomorrow
                mCal.setTime(today);
                mCal.add(Calendar.DATE, 1);
                break;
            case 2:
                //Next Week
                mCal.setTime(today);
                mCal.add(Calendar.DATE, 7);
                break;
            case 3:
                //Pick a date
                break;
            default:
        }
    }

    private void afterTimeSelect(Integer position) {
        switch(position) {
            case 0:
                //08:00AM
                mCal.set(Calendar.HOUR_OF_DAY, 8);
                mCal.set(Calendar.MINUTE, 0);
                break;
            case 1:
                //1:00PM
                mCal.set(Calendar.HOUR_OF_DAY, 13);
                mCal.set(Calendar.MINUTE, 0);
                break;
            case 2:
                //6:00PM
                mCal.set(Calendar.HOUR_OF_DAY, 18);
                mCal.set(Calendar.MINUTE, 0);
                break;
            case 3:
                //8:00PM
                mCal.set(Calendar.HOUR_OF_DAY, 20);
                mCal.set(Calendar.MINUTE, 0);
                break;
            case 4:
                //Pick a time
                break;
            default:
        }
    }
}
