package com.simonsickle.carrieradwarepreventer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduledCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("com.simonsickle.intent.action.REGISTER_ALARM") ||
                    intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                SetAlarm(context);
            } else if (intent.getAction().equals("com.simonsickle.intent.action.CANCEL_ALARM")) {
                CancelAlarm(context);
            }
        }
    }

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Set pending intent to start our service
        Intent intent = new Intent(context, ScheduledCheckService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_NO_CREATE);

        //Inexact repeating saves battery
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, ScheduledCheckReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
