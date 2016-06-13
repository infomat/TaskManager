package com.conestogac.mytask;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;



public class ScheduledService extends IntentService {

    public ScheduledService() {
        super("ScheduledService");
    }

    @Override
    protected void onHandleIntent(Intent i) {
        Log.d(getClass().getSimpleName(), "scheduled work begins");
        Integer _id = i.getIntExtra(AlarmReceiver.EXTRA_ID, 0);

        Intent intent = new Intent();
        //To get data item with id at popup msg
        intent.putExtra(AlarmReceiver.EXTRA_ID, _id);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(this, AlarmPopupActivity.class);
        intent.setComponent(cn);
        startActivity(intent);

        Log.d(getClass().getSimpleName(), "scheduled work ends");
        AlarmReceiver.completeWakefulIntent(i);
    }

}
