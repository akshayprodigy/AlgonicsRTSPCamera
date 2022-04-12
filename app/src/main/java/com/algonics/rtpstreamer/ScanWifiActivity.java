package com.algonics.rtpstreamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.algonics.rtpstreamer.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScanWifiActivity extends BaseListActivity {

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView list;
    TextView alert,header;
    Button buttonEmail;
    Button buttonScan;
    String wifis[];
    Spinner spinner;

    SharedPreferences sharedPref;// = getSharedPreferences("RTSP4k",Context.MODE_PRIVATE);
    EditText pass;
    TextInputLayout newPass;
    AlertDialog.Builder builder;
    private ProgressBar pgsBar;

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.CHANGE_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);
        sharedPref = getSharedPreferences("RTSP4k",Context.MODE_PRIVATE);
        pgsBar = (ProgressBar) findViewById(R.id.pBar);
        list = getListView();
//        alert = findViewById(R.id.textViewalert);
        header = findViewById(R.id.textViewwifiList);
        buttonEmail = findViewById(R.id.buttonEmail);
        buttonEmail.setEnabled(false);
        buttonScan = findViewById(R.id.buttonSacn);
        buttonScan.setEnabled(false);
        header.setVisibility(View.VISIBLE);
//        alert.setVisibility(View.GONE);
        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        builder = new AlertDialog.Builder(this);
//        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String ssid = ((TextView) view).getText().toString();
//                connectToWifi(ssid);
//                Toast.makeText(ScanWifiActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

        // listening to single list item on click
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                // selected item
//
//
//                String ssid = ((TextView) view).getText().toString();
//
//
//                connectToWifi(ssid);
////                String networkSSID  = sharedPref.getString("networkSSID", "");
////                if(ssid.equals(networkSSID)){
////
////                    String networkPass  = sharedPref.getString("networkPass", "");
////                    Log.e("RTSP","networkSSID: "+networkSSID+" networkPass: "+networkPass );
////                    finallyConnect(networkSSID, networkPass);
////                }else {
////                    connectToWifi(ssid);
////                    Toast.makeText(ScanWifiActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();
////                }
//            }
//        });
        scanWifi();
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
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

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }




    public void StartScaningWifi(View view) {
        scanWifi();
    }

    public void Savedwificlicked(View view) {
        String networkSSID  = sharedPref.getString("networkSSID", "");
        connectToWifi(networkSSID);
    }

    public void EmailScanedWifi(View view) {

        String teststring = " wifi list of data = ";
        StringBuilder sb = new StringBuilder();
        for (String s : wifis) {
            if (s != null) {
                sb.append(s).append('\n');
            }
        }
        String itemList = sb.toString();

        Utils.sendReport(this,"Health Watcher",teststring + "\n"+ itemList );

//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("message/rfc822");
//        i.putExtra(Intent.EXTRA_EMAIL, new String[]{Utils.emailId});
//        i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher");
//        i.putExtra(Intent.EXTRA_TEXT, teststring + "\n"+ itemList);
//        try {
//            startActivity(Intent.createChooser(i, "Send mail..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(ScanWifiActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void scanWifi(){
        try{
            buttonEmail.setEnabled(false);
            buttonScan.setEnabled(false);
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            } else{
                LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}
                if(!gps_enabled && !network_enabled) {
                    // notify user
//                    new AlertDialog.Builder(this)
//                            .setMessage("Location Turned Off. Please Turn on the Location for Wifi Scanning.")
//                            .setPositiveButton("Open", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                                    startActivity (new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                                }
//                            })
//                            .setNegativeButton("Ok",null)
//                            .show();

                    builder.setMessage("Please Turn on the Location for Wifi Scanning.")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity (new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("Cancel", null);

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Location Turned Off");
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
                        }
                    });
                    alert.show();

                }else {
                    if (mainWifiObj.isWifiEnabled()) {
                        pgsBar.setVisibility(View.VISIBLE);
                        mainWifiObj.startScan();
                        buttonEmail.setEnabled(true);
                        buttonScan.setEnabled(true);
                        //Toast.makeText(ScanWifiActivity.this, "Wifi SSID : ", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("RTSP", "Wifi not enabled");
                        //Setting message manually and performing action on button click
                        builder.setMessage("Please enable wifi from settings before proceeding")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //dialog.cancel();
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
                }
            }
        }catch (Exception e){

            builder.setMessage("Message: "+ e.getMessage())
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", null)
            ;

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Wifi bug");
            alert.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
                }
            });
            alert.show();

        }

    }
    String ssid;
    public void ConnectWifi(View view) {
//        String networkSSID  = sharedPref.getString("networkSSID", "");
//        if((ssid.equals(networkSSID))){
//            connectwithoutDialog();
//        }else {
//            connectToWifi(ssid);
//        }

        String savedpass = checkSavedWifis(ssid);

        if(savedpass != null){
            connectwithoutDialog(ssid,savedpass);
        }else {
            connectToWifi(ssid);
        }

    }

    public void connectwithoutDialog( String networkSSID, String networkPass){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                finallyConnect(networkPass, networkSSID);
            }
        }, 200);
    }

    // wifi receiver
    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            header.setVisibility(View.VISIBLE);
            pgsBar.setVisibility(View.GONE);
            buttonEmail.setEnabled(true);
//            alert.setVisibility(View.VISIBLE);
//            alert.setText("Scanning...");
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()];
            Log.d("Wifis", String.valueOf(wifiScanList.size()));
            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = ((wifiScanList.get(i)).toString());
            }
            List<String> filtered = new ArrayList<String>();
            for (String eachWifi : wifis) {
                String[] temp = eachWifi.split(",");
                String tempdata = temp[0].substring(5).trim();
                tempdata.trim();
                if(tempdata != null){
                    if(!tempdata.equals("")){
                        filtered.add(tempdata);
                    }
                }
            }
            String networkSSID  = sharedPref.getString("networkSSID", "");
            String timeStamp  = sharedPref.getString("timeStamp", "");
            if (filtered.contains(networkSSID)) {
                Log.d("Wifi","True");
                // sourav
//                alert.setVisibility(View.VISIBLE);
//                alert.setText(networkSSID+" was last used at "+ timeStamp);
//                alert.setText("Tap to connect last used : "+ networkSSID);
            }
            else {
                Log.d("Wifi","False");
            }

            spinner = (Spinner) findViewById(R.id.spinnerwifi);
            ArrayAdapter<String> adp = new ArrayAdapter<String> (getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,filtered);
            spinner.setAdapter(adp);

            spinner.setVisibility(View.VISIBLE);
            //Set listener Called when the item is selected in spinner
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long arg3)
                {
                    ssid =  parent.getItemAtPosition(position).toString();
                    buttonScan.setEnabled(true);
                    //Toast.makeText(parent.getContext(), ssid, Toast.LENGTH_LONG).show();

                }

                public void onNothingSelected(AdapterView<?> arg0)
                {
                    // TODO Auto-generated method stub
                }
            });
//            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.label, filtered));


        }
    }

    private void finallyConnect(String networkPass, String networkSSID) {
//        WifiConfiguration wifiConfig = new WifiConfiguration();
//        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
//        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
//
//        // remember id
//        int netId = mainWifiObj.addNetwork(wifiConfig);
//        mainWifiObj.disconnect();
//        mainWifiObj.enableNetwork(netId, true);
//        mainWifiObj.reconnect();
//
//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"" + networkSSID + "\"";
//        conf.preSharedKey = "\"" + networkPass + "\"";
//        mainWifiObj.addNetwork(conf);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", networkSSID);
            wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//remember id
            int netId = wifiManager.addNetwork(wifiConfig);
            wifiManager.disconnect();
            boolean status = wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            Log.e("RTSP", "networkSSID: " + networkSSID + " networkPass: " + networkPass + " status: " + status);
        }else {
            Log.e("TAG","connection wifi  Q");

            WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                    .setSsid( networkSSID )
                    .setWpa2Passphrase(networkPass)
                    .build();

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(wifiNetworkSpecifier)
                    .build();

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);

                    if(connectivityManager.bindProcessToNetwork(network)){
                        //alert.setVisibility(View.GONE);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String timestamp = simpleDateFormat.format(new Date());

                        saveWifis(networkSSID,networkPass,timestamp);

                        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo connectionInfo = wm.getConnectionInfo();
                        int ipAddress = connectionInfo.getIpAddress();
                        String ipString = Formatter.formatIpAddress(ipAddress);
                        DhcpInfo dhcp = wm.getDhcpInfo();
                        String gatewayIP = Formatter.formatIpAddress(dhcp.gateway);
                        Utils.serverip=gatewayIP;//ipString;
                        Intent myIntent = new Intent(ScanWifiActivity.this, LaunchingActivity.class);

                        //myIntent.putExtra("orientation", "Portrait"); //Optional parameters
                        ScanWifiActivity.this.startActivity(myIntent);
                    }
                    Log.e("TAG","onAvailable");
                }

                @Override
                public void onLosing(@NonNull Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                    Log.e("TAG","onLosing");
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    Log.e("TAG", "losing active connection");
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    if(isVisible){
                        Toast.makeText(ScanWifiActivity.this, "Could not connect to the Wifi, Incorrect Password or Timed out", Toast.LENGTH_SHORT).show();
                        Log.e("RTSP","onUnavailable");
                        Utils.serverip="";
                        connectToWifi(ssid);
                    }

                }


            };
//            connectivityManager.requestNetwork(networkRequest,networkCallback,10000);
            connectivityManager.requestNetwork(networkRequest,networkCallback);
        }
    }

    private void saveWifis(String networkSSID,String networkPass,String timestamp)
    {
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("networkSSID", networkSSID);
//        editor.putString("networkPass", networkPass);
////        editor.putString("timeStamp", format);
//        editor.apply();

        SharedPreferences.Editor editor = sharedPref.edit();
        String savedSSID1  = sharedPref.getString("networkSSID1",null);
        String savedSSID2  = sharedPref.getString("networkSSID2",null);
        String savedSSID3  = sharedPref.getString("networkSSID3",null);

        Boolean saved = false;

        if(savedSSID1 == null)
        {
            editor.putString("networkSSID1", networkSSID);
            editor.putString("networkPass1", networkPass);
            editor.apply();
            return;
        }
        else if(savedSSID1 != null && savedSSID1.equals(networkSSID))
        {
            editor.putString("networkSSID1", networkSSID);
            editor.putString("networkPass1", networkPass);
            editor.apply();
            return;
        }
        else if(savedSSID2 == null)
        {
            editor.putString("networkSSID2", networkSSID);
            editor.putString("networkPass2", networkPass);
            editor.apply();
            return;
        }
        else if(savedSSID2 != null && savedSSID2.equals(networkSSID))
        {
            editor.putString("networkSSID2", networkSSID);
            editor.putString("networkPass2", networkPass);
            editor.apply();
            return;
        }
        else if(savedSSID3 == null)
        {
            editor.putString("networkSSID3", networkSSID);
            editor.putString("networkPass3", networkPass);
            editor.apply();
            return;
        }
        else if(savedSSID3 != null && savedSSID3.equals(networkSSID))
        {
            editor.putString("networkSSID3", networkSSID);
            editor.putString("networkPass3", networkPass);
            editor.apply();
            return;
        }
        else
        {
            //Load in Last

            String savedPass1  = sharedPref.getString("networkPass1",null);
            String savedPass2  = sharedPref.getString("networkPass2",null);
            String savedPass3  = sharedPref.getString("networkPass3",null);

            editor.putString("networkSSID3", savedSSID2);
            editor.putString("networkPass3", savedPass2);
            editor.putString("networkSSID2", savedSSID1);
            editor.putString("networkPass2", savedPass1);
            editor.putString("networkSSID1", networkSSID);
            editor.putString("networkPass1", networkPass);
            editor.apply();
        }

    }

    private String checkSavedWifis(String networkSSID){
        String savedSSID1  = sharedPref.getString("networkSSID1",null);
        String savedSSID2  = sharedPref.getString("networkSSID2",null);
        String savedSSID3  = sharedPref.getString("networkSSID3",null);

        if(savedSSID1 != null && savedSSID1.equals(networkSSID))
        {
            return sharedPref.getString("networkPass1",null);
        }
        else if(savedSSID2 != null && savedSSID2.equals(networkSSID))
        {
            return sharedPref.getString("networkPass2",null);
        }
        else if(savedSSID3 != null && savedSSID3.equals(networkSSID))
        {
            return sharedPref.getString("networkPass3",null);
        }

        return null;

    }

    private void connectToWifi(final String wifiSSID) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);


        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        newPass = (TextInputLayout) dialog.findViewById(R.id.textPassword);
        pass = newPass.getEditText();
//        pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText(wifiSSID);
        String networkSSID  = sharedPref.getString("networkSSID", "");
        if(wifiSSID.equals(networkSSID)){
            String networkPass  = sharedPref.getString("networkPass", "");
            pass.setText(networkPass);
        }
        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                if(checkPassword!=null && !checkPassword.equals("")){
                    finallyConnect(checkPassword, wifiSSID);
                    dialog.dismiss();
                }else {
                    builder.setMessage("Password cannot be empty")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", null)
                    ;

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Empty Password");
                    alert.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
                        }
                    });
                    alert.show();
                }

            }
        });
        dialog.show();
    }



}