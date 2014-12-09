package com.simonsickle.carrieradwarepreventer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    public final String PREFS_NAME = "ignition_malware";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set instance of package manager
        PackageManager pm = getPackageManager();

        try {
            pm.getPackageInfo(getString(R.string.malware_name), PackageManager.GET_ACTIVITIES);
            if (pm.getPackageInfo(getString(R.string.malware_name), PackageManager.GET_META_DATA).applicationInfo.enabled) {
                Button btnFix = (Button) findViewById(R.id.btnFix);
                TextView txtInfo = (TextView) findViewById(R.id.txtInformation);
                btnFix.setVisibility(View.VISIBLE);
                txtInfo.setTextColor(Color.RED);
                txtInfo.setText(getString(R.string.infected_enabled));

                // Button is clicked
                btnFix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("user_disabled_before", true);
                        editor.apply();

                        try {
                            //Open the specific App Info page to allow disable
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getString(R.string.malware_name)));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                //Save to shared prefs
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("user_disabled_before", true);
                editor.apply();

                //Update GUI
                Button btnFix = (Button) findViewById(R.id.btnFix);
                TextView txtInfo = (TextView) findViewById(R.id.txtInformation);
                btnFix.setVisibility(View.GONE);
                txtInfo.setTextColor(Color.RED);
                txtInfo.setText(getString(R.string.infected_disabled));
            }
        } catch (PackageManager.NameNotFoundException e) {
            //If were here, no package matches that name
            Button btnFix = (Button) findViewById(R.id.btnFix);
            TextView txtInfo = (TextView) findViewById(R.id.txtInformation);
            btnFix.setVisibility(View.GONE);
            txtInfo.setText(getString(R.string.not_infected));
        }

        //On open, call the intent
        Intent intent = new Intent(getApplicationContext(), ScheduledCheckReceiver.class);
        intent.setAction("com.simonsickle.intent.action.RegisterAlarm");
        sendBroadcast(intent);

    }
}