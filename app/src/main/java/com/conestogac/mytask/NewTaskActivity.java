package com.conestogac.mytask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This activity is for getting task from user
 */
public class NewTaskActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_DB_COMMAND = "dboperation";
    public static final String EXTRA_SELECTED_ITEM = "selectedItem";
    public static final String[] DbOperation = {"CREATE", "UPDATE"};

    private static final String TAG = NewTaskActivity.class.getSimpleName();
    private static final String[] listPriority={"None", "Low", "Mid", "Important"};
    private static String[] listDate={"Today", "Tomorrow", "Next", "Pick a date..."};
    private static final String[] listTime={"Morning 08:00AM", "Afternoon 1:00PM", "Evening 6:00PM", "Night 8:00PM", "Pick a time..."};
    private SimpleDateFormat sdf_dayOfWeek = new SimpleDateFormat("EEEE");
    private SimpleDateFormat sdf_user = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
    private SimpleDateFormat sdf_date = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    private SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm a");

    private Date today = new Date();
    Task taskItem = new Task();;

    private EditText edTodo;
    private TextView tvPriority;
    private TextView tvDate;
    private TextView tvTime;
    private Spinner spPriority;
    private Spinner spDate;
    private Spinner spTime;
    private Button  btAdd;

    private Integer mPriority = 0;
    private Calendar mCal;
    private TaskDatabaseHelper mytaskdb;

    private AlarmManager alarms=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtask);

        //create instance of db
        mytaskdb = new TaskDatabaseHelper(this);

        //set up widget
        setUpWidget();
        setupSpinner();

        //get Calendar instance
        mCal = Calendar.getInstance();

        //Define Alarm Manager
        alarms=(AlarmManager)getSystemService(ALARM_SERVICE);

        //check Extra and set data if it is update
        if (DbOperation[1].equals(getIntent().getStringExtra(EXTRA_DB_COMMAND))) {
            this.setTitle("Update Task");
            btAdd.setText("UPDATE");
            taskItem = (Task) getIntent().getExtras().get(EXTRA_SELECTED_ITEM);

            //set todo
            edTodo.setText(taskItem.getTodo());
            //set calendar
            try {
                mCal.setTime(sdf_user.parse(taskItem.getDueDateTime()));
            } catch (Exception e) {
                mCal.setTime(today);
            }

        //to make simple, else is considerd as New Task
        } else {
            this.setTitle("New Task");
            btAdd.setText("ADD");
            //Set Default Value
            taskItem.setPriority(0);
            mCal.setTime(today);
        }

        //set priority and Date Time
        tvPriority.setText(listPriority[taskItem.getPriority()]);
        tvDate.setText(sdf_date.format(mCal.getTime()));
        tvTime.setText(sdf_time.format(mCal.getTime()));
    }

    private void setUpWidget() {
        edTodo = (EditText) findViewById(R.id.edTodo);
        tvPriority = (TextView) findViewById(R.id.tvPriority);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btAdd = (Button) findViewById(R.id.btAdd);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent,
            View v, int position, long id) {
        parent.getItemAtPosition(position);

        switch(parent.getId()) {

            case R.id.spPriority:
                Log.d(TAG,"onItemSelected - Priority: "+position);
                afterPrioritySelect(position);
                break;

            case R.id.spDate:
                Log.d(TAG,"onItemSelected - Date: "+position);
                afterDateSelect(position);
                break;

            case R.id.spTime:
                Log.d(TAG,"onItemSelected - Time: "+position);
                afterTimeSelect(position);
                break;
        }
        return;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //To make spinner inflated after user select textview
    //Actually, make spinner use customized view instead of fixed array items is a right way
    //To make simple I use this way
    //When Priority Textview Selected
    public void onPriority(View view) {
        Log.d(TAG, "onPriority");
        spPriority.performClick();
        Log.d(TAG, "onPriority- After Perform click");
    }

    //When Date Textview Selected
    public void onDate(View view) {
        boolean retValue;
        Log.d(TAG, "onDate");
        spDate.performClick();
        Log.d(TAG, "onDate- After Perform click");
    }

    //When Time Textview Selected
    public void onTime(View view) {
        Log.d(TAG, "onTime");
        spTime.performClick();
        Log.d(TAG, "onDate- After Perform click");
    }

    //Add Button
    public void onAddTask(View view) {
        String strDate;

            Log.d(TAG, "Add Button");
            taskItem.setTodo(edTodo.getText().toString());
            taskItem.setPriority(mPriority);

            strDate = sdf_user.format(mCal.getTime());
            Log.d(TAG, "ADD Date: " + strDate);
            taskItem.setDueDateTime(strDate);

        if(btAdd.getText().equals("ADD")) {
            //After insert,
            taskItem.setId(mytaskdb.insertTask(taskItem));

            //pass markitem object to data helper
            if (taskItem.getId() > 0) {
                Toast.makeText(getApplicationContext(), "Task is added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error!!! Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mytaskdb.updateTask(taskItem) > 0) {
                //Before create updated alarm, cancel previous alarm
                cancelAlarm();
                Toast.makeText(getApplicationContext(), "Task is updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error!!! Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        startAlarm(); //Start Alarm
        this.finish();
    }

    //Complete Button
    public void onCompleteTask(View view) {
        Log.d(TAG, "Complete Button");

        cancelAlarm();  //Cancel Alarm first incase of probelm at DB

        if (mytaskdb.deleteTask(taskItem.getId()) > 0) {
            Toast.makeText(getApplicationContext(), "Task is deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No matched item to delete!!!", Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }

    // To setup spinner to get Priority, Data, Time
    private void setupSpinner() {
        //Get Spinner and set listener to get event
        spPriority=(Spinner)findViewById(R.id.spPriority);
        spDate=(Spinner)findViewById(R.id.spDate);
        spTime=(Spinner)findViewById(R.id.spTime);


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
        //These Set should be in the order to avoid first event without user interaction.
        //i.e always position 0 selection event will come without following these order.
        spPriority.setAdapter(aaPriority);
        spPriority.setSelection(0,false);
        spPriority.setOnItemSelectedListener(this);

        aaListDate.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(aaListDate);
        spDate.setSelection(0,false);
        spDate.setOnItemSelectedListener(this);

        aaListTime.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(aaListTime);
        spTime.setSelection(0,false);
        spTime.setOnItemSelectedListener(this);
    }


    private void afterPrioritySelect(Integer position) {
        Log.d(TAG, "afterPrioritySelect()");
        switch(position) {
            case 0: case 1: case 2: case 3:
                mPriority = position;
                break;

            default:
                mPriority = 0;
        }
        tvPriority.setText(listPriority[mPriority]);
        Log.d(TAG, "afterPrioritySelect() -- SetText");
    }

    private void afterDateSelect(Integer position) {
        Log.d(TAG, "afterDateSelect()");

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
                new DatePickerDialog(this, d, mCal.get(Calendar.YEAR),
                        mCal.get(Calendar.MONTH),
                        mCal.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            default:
        }
        //set textview for date from
        tvDate.setText(sdf_date.format(mCal.getTime()));
        Log.d(TAG, "afterDateSelect() -- SetText");
    }

    //Date Picker Listener
    //Update mCal member variable and update textview
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            //Update mCal
            mCal.set(Calendar.YEAR, year);
            mCal.set(Calendar.MONTH, monthOfYear);
            mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            //Updating textview
            tvDate.setText(sdf_date.format(mCal.getTime()));
            Log.d(TAG, "DatePickerDialog() -- SetText");
        }
    };

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
                new TimePickerDialog(this, t,
                        mCal.get(Calendar.HOUR_OF_DAY),
                        mCal.get(Calendar.MINUTE), false)
                        .show();
                break;
            default:
        }

        //set textview for time from
        tvTime.setText(sdf_time.format(mCal.getTime()));
    }

    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {

            mCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCal.set(Calendar.MINUTE, minute);

            //Updating textview
            tvTime.setText(sdf_time.format(mCal.getTime()));

            Log.d(TAG, "TimePickerDialog() -- SetText");
        }
    };


    private void startAlarm() {
        AlarmReceiver.scheduleExactAlarm(this, alarms, mCal.getTimeInMillis(), taskItem.getId());
    }

    private void cancelAlarm() {
        AlarmReceiver.cancelAlarm(this, alarms, taskItem.getId());
    }

}
