package com.simonsickle.carrieradwarepreventer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    // The package name of the "malware" is com.LogiaGroup.LogiaDeck
    private final String malwarePackageName = "com.LogiaGroup.LogiaDeck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (detectIfAffected()) {
            Button btnFix = (Button) findViewById(R.id.btnFix);
            TextView txtInfo = (TextView) findViewById(R.id.txtInformation);
            btnFix.setVisibility(View.VISIBLE);
            txtInfo.setTextColor(Color.RED);
            txtInfo.setText("Infected! Click Fix It and then disable.");

            // Button is clicked
            btnFix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Open the specific App Info page to allow disable
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + malwarePackageName));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Button btnFix = (Button) findViewById(R.id.btnFix);
            TextView txtInfo = (TextView) findViewById(R.id.txtInformation);
            btnFix.setVisibility(View.GONE);
            txtInfo.setText("You are not infected. Give your carrier a high five!");
        }

    }

    // detect if affected
    private boolean detectIfAffected() {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(malwarePackageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}