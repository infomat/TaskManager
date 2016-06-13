package com.conestogac.mytask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


/*
 * This WakefulBroadcastReceiver receiver Helper for the common pattern of implementing
 * a BroadcastReceiver that receives a device wakeup event and then passes the work off to a Service,
 * while ensuring that the device does not go back to sleep during the transition.
 * A WakefulBroadcastReceiver uses the method startWakefulService() to start the service that does
 * the work. This method is comparable to startService(), except that the WakefulBroadcastReceiver
 * is holding a wake lock when the service starts. The intent that is passed with startWakefulService(ScheduledService)
 * holds an extra identifying the wake lock.
 * This class takes care of creating and managing a partial wake lock for you; you must request
 * the WAKE_LOCK permission to use it.
 * Additionally, after booting, to keep the alarm, it requires permisson RECEIVE_BOOT_COMPLETED
 * , and intent filter is defined for this broadcast receiver
 * https://developer.android.com/reference/android/support/v4/content/WakefulBroadcastReceiver.html
 *
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    static final String EXTRA_PERIOD="period";
    static final String EXTRA_ID="_id";
    static final Integer REPEATING_PERIOD = 40*60*1000; //every 40 minutes, showing msg

    @Override
    public void onReceive(Context ctxt, Intent i) {
        Integer _id = i.getIntExtra(EXTRA_ID, 0);
        Log.d(TAG,"At onReceive _ID "+_id);

        //Todo After booting, restore alarm list is needed
        //Due to shortage of time this will not implemented

        //To avoid app is killed due to invalid id check _id is valid.
        //In some cases, when alarm is updated, id can be wrong
        if (_id != 0) {
            startWakefulService(ctxt,
                    new Intent(ctxt, ScheduledService.class)
                            .putExtra(EXTRA_ID, _id));
        }
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
        //after first fire of alarm, it will repeat with REPEATING_PERIOD until alarm is deleted
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, alarmMilliSecTime, REPEATING_PERIOD, pi);
        Log.d(TAG,"Start Alarm Id: "+_id);
    }


    static void cancelAlarm(Context ctxt, AlarmManager alarms, Integer _id) {
        Intent i=new Intent(ctxt, AlarmReceiver.class);

        //get pending indent with given _id which is Primary key of each task item
        PendingIntent pi=PendingIntent.getBroadcast(ctxt, _id, i, 0);

        //using pending indent, cancel alarm
        alarms.cancel(pi);
        Log.d(TAG,"Canceled Alarm Id: "+_id);
    }
}
