package com.simonsickle.carrieradwarepreventer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class ScheduledCheckService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        runDisabledCheck();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void runDisabledCheck() {
        final String PREFS_NAME = "ignition_malware";

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (preferences.getBoolean("user_disabled_before", false)) {
            PackageManager pm = getPackageManager();
            boolean app_installed;
            try {
                pm.getPackageInfo(getString(R.string.malware_name), PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }

            if (app_installed) {
                try {
                    if (pm.getPackageInfo(getString(R.string.malware_name),
                            PackageManager.GET_META_DATA).applicationInfo.enabled) {
                        //Notify user that package is re-enabled
                        Intent resultIntent = new Intent(this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle(getString(R.string.notification_title))
                                        .setContentText(getString(R.string.notification_content))
                                        .setAutoCancel(true);

                        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        //Show the notification
                        mgr.notify(0, mBuilder.build());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}