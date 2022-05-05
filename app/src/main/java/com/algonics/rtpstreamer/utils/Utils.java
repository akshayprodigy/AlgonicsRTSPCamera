package com.algonics.rtpstreamer.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.algonics.rtpstreamer.ScanWifiActivity;

public class Utils {

    public static String serverip="";
    public static  String  healthport = ":8000/health/";
    public static  String  congestionport = ":8000/congestion/health/";
    public static long healthpackettimeout = 5 * 1000; // 5 seconds
    public static final long fpsimeout = 1 * 1000;
    public static String emailId ="sourav433@gmail.com";
    public static final long CongetiontastInterval = 1 * 1000; // 5 seconds;
    public static  final int minFps = 1;
    public static  final int maxFps = 30;
    public static  final float maxcongetionlimit = 0.7f;
    public static  final float mincongetionlimit = 0.3f;
    public static  final int waitperiod = 5;
    public static void sendReport(Context context, String header, String msg)
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        i.putExtra(Intent.EXTRA_SUBJECT, header);
        i.putExtra(Intent.EXTRA_TEXT, msg);
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
