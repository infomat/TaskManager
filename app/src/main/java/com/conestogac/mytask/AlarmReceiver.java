package com.conestogac.mytask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    static final String EXTRA_PERIOD="period";
    static final String EXTRA_ID="_id";
    //static final Integer REPEATING_PERIOD = 40*60*1000; //every 40 minutes, showing msg
    static final Integer REPEATING_PERIOD = 5*60*1000;  //TEST~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onReceive(Context ctxt, Intent i) {
        Integer _id = i.getIntExtra(EXTRA_ID, 0);
        Log.d(TAG,"At onReceive _ID "+_id);
        startWakefulService(ctxt,
                new Intent(ctxt, ScheduledService.class)
                        .putExtra(EXTRA_ID, _id));
    }


    static void scheduleExactAlarm(Context ctxt, AlarmManager alarms,
                                   long alarmMilliSecTime, Integer _id) {
        //Create intent
        Intent i=new Intent(ctxt, AlarmReceiver.class)
                .putExtra(EXTRA_PERIOD, alarmMilliSecTime)
                .putExtra(EXTRA_ID, _id);

        //Manage Alarm by _id of task
        PendingIntent pi=PendingIntent.getBroadcast(ctxt, _id.intValue(), i, 0);

        Log.d(TAG,"At scheduleExactAlarm _ID "+_id);
        Log.d(TAG, "System  "+System.currentTimeMillis());
        Log.d(TAG, "Alarm "+alarmMilliSecTime);
        Log.d(TAG, "System - Alarm "+(alarmMilliSecTime - System.currentTimeMillis()));

        //Depends on ELAPSED_REALTIME_WAKEUP or RTC_WAKEUP, used reference time should be different
        //For ELAPSED_REALTIME_WAKEUP,  elasped time should be used,
        //but for RTC_WAKEUP, system time should be used.
        //Accodring to https://developer.android.com/reference/android/os/SystemClock.html
        //ELAPSED_REALTIME_WAKEUP is more reliable than RTC, but RTC is recommend for alarm application
        //which uses network's time
        //alarms.setExact(AlarmManager.RTC_WAKEUP, alarmMilliSecTime, pi);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, alarmMilliSecTime, REPEATING_PERIOD, pi);
        Log.d(TAG,"Start Alarm Id: "+_id);
    }

    static void cancelAlarm(Context ctxt, AlarmManager alarms, Integer _id) {
        Intent i=new Intent(ctxt, AlarmReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(ctxt, _id, i, 0);
        alarms.cancel(pi);
        Log.d(TAG,"Canceled Alarm Id: "+_id);
    }

}
