package com.conestogac.mytask;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.commonsware.cwac.wakeful.*;

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

        //create instance of db
        mytaskdb = new TaskDatabaseHelper(this);

        //get random number between 0~insultMsg.length-1
        msgIndex = r.nextInt(insultMsg.length);
        //set dialog title
        this.setTitle(insultMsg[msgIndex]);

        tvTodo = (TextView) findViewById(R.id.tvTodo);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);

        alarmId = getIntent().getIntExtra(AlarmReceiver.EXTRA_ID, 0);
        mytask = mytaskdb.getData(alarmId);

        tvTodo.setText(mytask.getTodo());
        tvDateTime.setText(mytask.getDueDateTime());
    }
}
