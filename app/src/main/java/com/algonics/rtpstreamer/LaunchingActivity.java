package com.algonics.rtpstreamer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;

import com.algonics.rtpstreamer.utils.Utils;

public class LaunchingActivity extends baseActivity {



    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wm.getDhcpInfo();
        String ipAddress = Formatter.formatIpAddress(dhcp.ipAddress);
        String serverAddress = Formatter.formatIpAddress(dhcp.serverAddress);
        String gateway = Formatter.formatIpAddress(dhcp.gateway);
        String netmask = Formatter.formatIpAddress(dhcp.netmask);
        String dns1 = Formatter.formatIpAddress(dhcp.dns1);
        String dns2 = Formatter.formatIpAddress(dhcp.dns2);

        if(gateway.equals("0.0.0.0"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enable and select wifi from settings before proceeding")
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
                            startActivity (new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Ok", null)
            ;

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Wifi not enabled");
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
                }
            });
            alert.show();
        }
//
//        Utils.serverip=gateway;//ipString;


//        String msg = "IP Address : "+ipAddress+", Server Address : "+serverAddress+
//                ", Gateway : "+gateway+", Netmask : "+netmask+"," +
//                " DNS1 : "+dns1+", DNS2 : "+dns2;
//        Utils.sendReport(this,"Wifi Info",msg );


    }


    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void StartStreamingActivity(View view) {
        Intent myIntent = new Intent(LaunchingActivity.this, Camera2DemoActivity.class);
        //myIntent.putExtra("orientation", "Portrait"); //Optional parameters
        LaunchingActivity.this.startActivity(myIntent);
    }

    public void StartStreamingActivitylandscape(View view) {
        Intent myIntent = new Intent(LaunchingActivity.this, Camera2LandscapeActivity.class);
        //myIntent.putExtra("orientation", "Landscape"); //Optional parameters
        LaunchingActivity.this.startActivity(myIntent);
    }

    public void StartStreamingFileTransfer(View view) {
        Intent myIntent = new Intent(LaunchingActivity.this, RTSPFileTransferActivity.class);
        //myIntent.putExtra("orientation", "Landscape"); //Optional parameters
        LaunchingActivity.this.startActivity(myIntent);
    }
}