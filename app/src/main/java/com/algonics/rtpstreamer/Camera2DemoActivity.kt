package com.algonics.rtpstreamer

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.algonics.encoder.input.video.CameraHelper
import com.algonics.rtplibrary.util.BitrateAdapter
import com.algonics.rtplibrary.util.CongetionDataOfAllClients
import com.algonics.rtplibrary.view.OpenGlView
import com.algonics.rtpstreamer.rtspserver.RtspServerCamera2
import com.algonics.rtpstreamer.utils.Utils
import com.algonics.rtsp.rtsp.VideoCodec
import com.algonics.rtsp.utils.ConnectCheckerRtsp
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_camera2_demo.*
import kotlinx.android.synthetic.main.activity_camera_demo.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Camera2DemoActivity :baseActivity(), ConnectCheckerRtsp, View.OnClickListener,
        SurfaceHolder.Callback  {

    private var rtspServerCamera2:RtspServerCamera2? = null
    private var button: Button? = null
    private var bRecord: Button? = null
    private var currentDateAndTime = ""
    private var width = 0
    private var height = 0
    lateinit var myJob: Job ;
    //val sharedPref: SharedPreferences? = null
    private var fps =60;
    lateinit var mainHandler: Handler
    private var url = "http://serverip:8000/health"
    private var bitrate = 8 * 1000 * 1024;
    lateinit var spinner: Spinner
    var userIsInteracting:Boolean = false
    private var openGlView: OpenGlView? = null
    private val folder =
            File(Environment.getExternalStorageDirectory().absolutePath + "/rtmp-rtsp-stream-client-java")

    override fun onUserInteraction() {
        super.onUserInteraction()
        userIsInteracting = true
    }
    private var bitrateAdapter: BitrateAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera2_demo)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Rtsp Streaming"
        button = findViewById(R.id.b_start_stop2)
        button?.setOnClickListener(this)
        openGlView = findViewById(R.id.surfaceViewGL)
        //bRecord = findViewById(R.id.b_record)
        bRecord?.setOnClickListener(this)
        //switch_camera.setOnClickListener(this)
        rtspServerCamera2 = RtspServerCamera2(surfaceViewGL,this,1935)//RtspServerCamera2(this ,true, this, 1935)
        rtspServerCamera2?.setVideoCodec(VideoCodec.H265)
        surfaceViewGL.holder.addCallback(this)
        //openGlView.holder.addCallback(this)
        // access the items of the list
        val languages = resources.getStringArray(R.array.Languages)
        val sharedPref = getSharedPreferences("RTSP4k", MODE_PRIVATE)
        val spinerValue = sharedPref.getInt("spinerValue", 0)
        val editor:SharedPreferences.Editor =  sharedPref.edit()
        Log.i("Spinner",spinerValue.toString())

        // access the spinner
        spinner = findViewById<Spinner>(R.id.spinner2)

        Log.i("SelectedSpinner",spinerValue.toString())
        Log.i("SelectedSpinnerActual",spinner.selectedItemPosition.toString())
        if (spinner != null) {

            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, languages)
//            spinner.adapter = adapter
            spinner.adapter = adapter
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    // this code will be executed after 2 seconds
                    spinner.setSelection(spinerValue,true)
                }
            }, 200)
//            spinner.setSelection(spinerValue,true)
            spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {

                    // Landscape
                    // 1) 1280 * 960  60 FPS - 8 Bitrate

                    if (userIsInteracting){
                        editor.putInt("spinerValue",position)
//                    editor.apply()
                        var x = editor.commit()
                        Log.i("Commit",x.toString())
                        Log.i("Position",position.toString())
                        Log.i("Shared", sharedPref.getInt("spinerValue", 0).toString())
                    }

                    if (position == 0) {
                        width = 1280
                        height = 960
                        fps = 60
                        bitrate = 8 * 1000 * 1024;

                    }
                    // 2) 1920 * 1080 60 FPS - 8 Bitrate
                    else if (position == 1) {
                        width = 1920
                        height = 1080
                        fps = 60
                        bitrate = 8 * 1000 * 1024;
                    }
                    // 3) 4k 10 FPS
                    else if (position == 2){
                        width = 3840
                        height = 2160
                        fps = 10
                        bitrate = 8 * 1000 * 1024;
                    }
                    // 4) 4k 20 FPS
                    else if (position == 3){
                        width = 3840
                        height = 2160
                        fps = 20
                        bitrate = 8 * 1000 * 1024;
                    }
                    // 5) 4k 24 FPS
                    else if (position == 4){
                        width = 3840
                        height = 2160
                        fps = 24
                        bitrate = 8 * 1000 * 1024;
                    }
                    // 6) 4k 24 FPS - 35 Bitrate
                    else if (position == 5){
                        width = 3840
                        height = 2160
                        fps = 24
                        bitrate = 35 * 1000 * 1024;
                    }
                    // 7) 4k 30 FPS - 8 Bitrate
                    else if (position == 6){
                        width = 3840
                        height = 2160
                        fps = 30
                        bitrate = 8 * 1000 * 1024;
                    }
                    // 8) 4k 30 FPS - 12 Bitrate
                    else if (position == 7){
                        width = 3840
                        height = 2160
                        fps = 30
                        bitrate = 12 * 1000 * 1024;
                    }
                    // 9) 4k 30 FPS - 14 Bitrate
                    else if (position == 8){
                        width = 3840
                        height = 2160
                        fps = 30
                        bitrate = 14 * 1000 * 1024;
                    }
                    // 10) 4k 30 FPS - 16 Bitrate
                    else if (position == 9){
                        width = 3840
                        height = 2160
                        fps = 30
                        bitrate = 16 * 1000 * 1024;
                    }
                    // 11) 4k 30 FPS - 35 Bitrate
                    else {
                        //56 not working
                        width = 3840
                        height = 2160
                        fps = 30
                        bitrate = 35 * 1000 * 1024;
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        Log.i("SelectedSpinner",spinerValue.toString())
        spinner.setSelection(spinerValue)

//        width = 1920
//        height = 1080
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    override fun onResume() {
        super.onResume()
        spinner.setSelection(0);
        width = 1920
        height = 1080
        fps = 60
        bitrate = 8 * 1000 * 1024;
    }

    override fun onNewBitrateRtsp(bitrate: Long) {

    }

    override fun onConnectionStartedRtsp(rtspUrl: String) {
        TODO("Not yet implemented")
        Log.e("rtsp server ","onConnectionStartedRtsp: "+rtspUrl)
    }

    /** A safe way to get an instance of the Camera object.
    fun getCameraInstance(): Camera? {
    return try {
    Camera.open() // attempt to get a Camera instance
    } catch (e: Exception) {
    // Camera is not available (in use or does not exist)
    null // returns null if camera is unavailable
    }
    }*/
/*
    override fun onNewBitrateRtsp(bitrate: Long) {
        if (bitrateAdapter != null) bitrateAdapter!!.adaptBitrate(bitrate)
    }*/



    override fun onConnectionSuccessRtsp() {

        runOnUiThread {
            Toast.makeText(this@Camera2DemoActivity, "Connection success", Toast.LENGTH_SHORT).show()
        }
        //rtspServerCamera2.setVideoBitrateOnFly()
        bitrateAdapter = BitrateAdapter { bitrate -> sendCongetiondata() } //rtspServerCamera2!!.setVideoBitrateOnFly(bitrate)
        //bitrateAdapter!!.setMaxBitrate(rtspServerCamera2!!.getBitrate())
    }
    override fun onConnectionFailedRtsp(reason: String){
        runOnUiThread{
            //Log.e("RTSP4","onConnectionFailedRtsp: "+reason + "  stack: "+ Log.getStackTraceString(Exception()))
            Toast.makeText(this@Camera2DemoActivity, "Connection failed. $reason", Toast.LENGTH_SHORT)
                    .show()
            if (rtspServerCamera2!!.isStreaming){
                // will add logic when needed for streaing
            }else{
                rtspServerCamera2!!.stopStream()
                button?.setText(R.string.start_button)
                tv_url2.text = ""
                tv_fps2.text=""
                tv_resolution2.text=""
                if(!Utils.serverip.equals("")){
                    myJob.cancel();
                    mainHandler.removeCallbacks(updateTextTask)
                }


            }

        }
    }

    override fun onDisconnectRtsp() {
        runOnUiThread {
            Toast.makeText(this@Camera2DemoActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAuthErrorRtsp(){
        runOnUiThread{
            Toast.makeText(this@Camera2DemoActivity, "Auth error", Toast.LENGTH_SHORT).show()
            rtspServerCamera2?.stopStream()
            button!!.setText(R.string.start_button)
            tv_url2.text = ""
            tv_fps2.text=""
            tv_resolution2.text=""
            if(!Utils.serverip.equals("")){
                myJob.cancel();
                mainHandler.removeCallbacks(updateTextTask)
            }

        }
    }

    override fun onAuthSuccessRtsp() {
        runOnUiThread {
            Toast.makeText(this@Camera2DemoActivity, "Auth success", Toast.LENGTH_SHORT).show()
        }
    }

    @InternalCoroutinesApi
    override fun onClick(view: View){
        when (view.id){
            R.id.b_start_stop2 -> if (!rtspServerCamera2!!.isStreaming){
                spinner.isClickable = false
                spinner.isFocusable = false
//8 * 1000* 1024
                if (rtspServerCamera2!!.isRecording || rtspServerCamera2!!.prepareAudio() && rtspServerCamera2!!.prepareVideo(width,height,fps,bitrate ,CameraHelper.getCameraOrientation(this))) {
                    button!!.setText(R.string.stop_button)
                     //VideoCodec.H264
                    //rtspServerCamera1?.setVideoCodec(VideoCodec.H265)
                    //surfaceView.holder.addCallback(this)
                    rtspServerCamera2!!.startStream()
                    tv_url2.text = rtspServerCamera2?.getEndPointConnection()
                    tv_resolution2.text = width.toString() + "*" + height//1280*960
                    tv_fps2.text = fps.toString() + "->" + (bitrate / (1000*1024)).toString(); // + " -> " + "8";

                    //start posting health packets
                    if(!Utils.serverip.equals("")){
                        mainHandler = Handler(Looper.getMainLooper())
                        mainHandler.post(updateTextTask)

                        myJob = startCongetionSendingJob(Utils.CongetiontastInterval);
                    }



                }else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT)
                            .show()
                }
            }else {
                spinner.isClickable = true
                spinner.isFocusable = true
                button!!.setText(R.string.start_button)
                rtspServerCamera2!!.stopStream()
                tv_url2.text = ""
                tv_fps2.text = ""
                tv_resolution2.text = ""
                if(!Utils.serverip.equals("")){
                    myJob .cancel()
                    mainHandler.removeCallbacks(updateTextTask)
                }

            }
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
    }
    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        rtspServerCamera2!!.startPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        if (rtspServerCamera2!!.isRecording) {
            rtspServerCamera2!!.stopRecord()
            bRecord!!.setText(R.string.start_record)
            Toast.makeText(this, "file " + currentDateAndTime + ".mp4 saved in " + folder.absolutePath,
                    Toast.LENGTH_SHORT).show()
            currentDateAndTime = ""
        }
        if (rtspServerCamera2!!.isStreaming) {
            rtspServerCamera2!!.stopStream()
            button!!.text = resources.getString(R.string.start_button)
            tv_url2.text = ""
            tv_fps2.text = ""
            tv_resolution2.text = ""
            if(!Utils.serverip.equals("")){
                mainHandler.removeCallbacks(updateTextTask)
                myJob .cancel()
            }

        }
        rtspServerCamera2!!.stopPreview()
    }

    fun ScanWifiNetwork(view: View) {}


    private val updateTextTask = object : Runnable {
        override fun run() {
            minusOneSecond()
            mainHandler.postDelayed(this, Utils.healthpackettimeout)
        }
    }

    // Instantiate the RequestQueue.

    @InternalCoroutinesApi
    private fun startCongetionSendingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (NonCancellable.isActive) {
                // add your task here
                sendCongetiondata()
                delay(timeInterval)
            }
        }
    }

    fun sendCongetiondata(){
        val queue = Volley.newRequestQueue(this)
        val postData = JSONObject()
        try {
            val s = SimpleDateFormat("ddMMyyyyhhmmss")
            val format: String = s.format(Date())
            val congetiondata = CongetionDataOfAllClients();
            congetiondata.timeStamp = format;
            congetiondata.congetionDataList = rtspServerCamera2?.hasCongestion()?.congetionDataList

            val gson = Gson()
            val json: String = gson.toJson(congetiondata)
            Log.d("RTSP","url: post "+ json)
            var hascongsation: Boolean=false
            if(rtspServerCamera2?.hasCongestion()?.congetionDataList != null) {
                if (rtspServerCamera2?.hasCongestion()?.congetionDataList?.size!! >= 1) {
                    hascongsation = rtspServerCamera2?.hasCongestion()?.congetionDataList?.get(0)?.hasCongestion!!
                }
            }
            postData.put("congestion",hascongsation )
            postData.put("details",json )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        url= "http://"+Utils.serverip+ Utils.congestionport;
        //url = "http://192.168.29.127:8000/congestion/health/"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData,
            { response ->  Log.d("RTSP","response: "+response) }
        ) { error -> error.printStackTrace() }
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    fun minusOneSecond() {
       // post message here
        val queue = Volley.newRequestQueue(this)
        val postData = JSONObject()
        try {
            postData.put("ip", rtspServerCamera2?.getEndPointConnection())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        url= "http://"+Utils.serverip+ Utils.healthport;
        Log.d("RTSP","url: post "+url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData,
            { response ->  Log.d("RTSP","response: "+response) }
        ) { error -> error.printStackTrace() }
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    // end of class
}