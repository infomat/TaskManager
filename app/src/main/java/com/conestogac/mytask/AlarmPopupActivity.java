package com.conestogac.mytask;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Random;

public class AlarmPopupActivity extends Activity {

    private static final String[] insultMsg = {
            "Hurry!",
            "Rush!",
            "Move it!",
            "Shake a leg!",
            "Make it snappy!" };

    private Random r = new Random();
    private Integer msgIndex;

    private TextView tvTodo;
    private TextView tvDateTime;
    private TaskDatabaseHelper mytaskdb;
    private Integer alarmId;
    private Task mytask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_popup);

        //get random number between 0~insultMsg.length-1
        //set dialog title with random insult messsgae
        //However, alternative milder msessage is used instead of insult
        msgIndex = r.nextInt(insultMsg.length);
        this.setTitle(insultMsg[msgIndex]);

        //get task ID from intent.
        //the id was used for UNIQUE alarm ID
        alarmId = getIntent().getIntExtra(AlarmReceiver.EXTRA_ID, 0);

        //create instance of db and get mytask using ID
        mytaskdb = new TaskDatabaseHelper(this);
        mytask = mytaskdb.getData(alarmId);

        //Get widget object pointer
        //and set dialog content with todo and datetime
        tvTodo = (TextView) findViewById(R.id.tvTodo);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        tvTodo.setText(mytask.getTodo());
        tvDateTime.setText(mytask.getDueDateTime());
    }

    //When user select more time button at alarm popup
    //Process nothing, just finish activity
    public void onMoreTime(View view) {
        //finish activity
        this.finish();
    }

    //When user select complete button at alarm popup
    //Remove Alarm and Remove Item from DB
    public void onCompleteTask(View view) {
        //first cancel alarm to avoid probelm, when there is problem at db
        AlarmManager alarms = (AlarmManager)getSystemService(ALARM_SERVICE);
        AlarmReceiver.cancelAlarm(this, alarms, mytask.getId());

        //remove db
        if (mytaskdb.deleteTask(mytask.getId()) > 0) {
            Toast.makeText(getApplicationContext(), "Task is deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No matched item to delete!!!", Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }
}
