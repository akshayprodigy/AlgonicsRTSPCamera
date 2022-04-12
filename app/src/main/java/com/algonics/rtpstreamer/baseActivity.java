package com.algonics.rtpstreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.algonics.rtpstreamer.utils.Utils;

public class baseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
    }

    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e("error", ex.toString());
                    //send email here
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{Utils.emailId});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher Crash log");
                    i.putExtra(Intent.EXTRA_TEXT, "Crashlog"+ ex.toString());
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException e) {
//                        Toast.makeText(ScanWifiActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            };
}