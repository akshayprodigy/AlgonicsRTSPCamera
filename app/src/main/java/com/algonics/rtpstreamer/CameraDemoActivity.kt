package com.algonics.rtpstreamer

import android.hardware.Camera
import android.media.CamcorderProfile
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.algonics.encoder.input.video.CameraHelper
import com.algonics.rtsp.utils.ConnectCheckerRtsp
import com.algonics.rtpstreamer.rtspserver.RtspServerCamera1
import com.algonics.rtsp.rtsp.VideoCodec
import kotlinx.android.synthetic.main.activity_camera2_demo.*
import kotlinx.android.synthetic.main.activity_camera_demo.*
import java.io.File
import java.lang.Exception
import java.util.*

class CameraDemoActivity : AppCompatActivity(), ConnectCheckerRtsp, View.OnClickListener,
        SurfaceHolder.Callback {

    private var rtspServerCamera1: RtspServerCamera1? = null
    private var button: Button? = null
    private var bRecord: Button? = null
    private var currentDateAndTime = ""
    private var width = 0
    private var height = 0
    private var fps =60;
    private var bitrate = 8 * 1000 * 1024;
    private val folder =
            File(Environment.getExternalStorageDirectory().absolutePath + "/rtmp-rtsp-stream-client-java")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera_demo)
        button = findViewById(R.id.b_start_stop)
        button?.setOnClickListener(this)
        //bRecord = findViewById(R.id.b_record)
        bRecord?.setOnClickListener(this)
        //switch_camera.setOnClickListener(this)
        rtspServerCamera1 = RtspServerCamera1(surfaceView, this, 1935)
        surfaceView.holder.addCallback(this)
        var previews: MutableList<Camera.Size> = rtspServerCamera1!!.getResolutionsBack()
        for (size in previews) {
            Log.d("RTSP","size: "+ size.width+" height "+size.height)
        }

        // access the items of the list
        val languages = resources.getStringArray(R.array.Languages)

        // access the spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    // 1) 1280 * 960  60 FPS - 8 Bitrate
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
                    /*Toast.makeText(this,
                            getString(R.string.selected_item) + " " +
                                    "" + languages[position], Toast.LENGTH_SHORT).show()*/
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        //getMaxEncoderSizeSupported()
        width = 1920
        height = 1080
    }


    override fun onResume() {
        super.onResume()
        spinner.setSelection(0);
        width = 1920
        height = 1080
    }
    override fun onNewBitrateRtsp(bitrate: Long) {

    }

    override fun onConnectionStartedRtsp(rtspUrl: String) {
        TODO("Not yet implemented")
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

    override fun onConnectionSuccessRtsp() {
        runOnUiThread {
            Toast.makeText(this@CameraDemoActivity, "Connection success", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConnectionFailedRtsp(reason: String) {
        runOnUiThread {
            Toast.makeText(this@CameraDemoActivity, "Connection failed. $reason", Toast.LENGTH_SHORT)
                    .show()
            rtspServerCamera1!!.stopStream()
            button?.setText(R.string.start_button)
        }
    }

    override fun onDisconnectRtsp() {
        runOnUiThread {
            Toast.makeText(this@CameraDemoActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAuthErrorRtsp() {
        runOnUiThread {
            Toast.makeText(this@CameraDemoActivity, "Auth error", Toast.LENGTH_SHORT).show()
            rtspServerCamera1?.stopStream()
            button!!.setText(R.string.start_button)
            tv_url.text = ""
            tv_fps.text=""
            tv_resolution.text=""
        }
    }

    override fun onAuthSuccessRtsp() {
        runOnUiThread {
            Toast.makeText(this@CameraDemoActivity, "Auth success", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getMaxEncoderSizeSupported(): Int {
        return if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_2160P)) {
            //width =3840
            //height =2160
            Log.d("Camera","3840 x 2160" )
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
            //camera.Size(1920, 1080)
            //width =1920
            //height =1080
            Log.d("Camera","1920 x 1080" )
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            //camera.Size(1280, 720)
            //width =1280
            //height =720
            Log.d("Camera","1280 x 720" )
        } else {
            //camera.Size(640, 480)
            //width =640
            //height =480
            Log.d("Camera","640 x 480" )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View) {
        when (view.id) {

                R.id.b_start_stop -> if (!rtspServerCamera1!!.isStreaming) {
                    spinner.isClickable = false
                    spinner.isFocusable = false//1200 8000
                    if (rtspServerCamera1!!.isRecording || rtspServerCamera1!!.prepareAudio() && rtspServerCamera1!!.prepareVideo(width, height, fps, bitrate, CameraHelper.getCameraOrientation(this))) {
                        button!!.setText(R.string.stop_button)
                        rtspServerCamera1!!.setVideoCodec(VideoCodec.H264) //VideoCodec.H264
                        try{
                            rtspServerCamera1!!.startStream()
                            tv_url.text = rtspServerCamera1?.getEndPointConnection()
                        }catch (e:Exception){
                            tv_url.text = "Not able to stream"
                            Log.e("rtsp server","Exception: "+e)
                        }
                        tv_resolution2.text = width.toString() + "*" + height//1280*960
                        tv_fps2.text = fps.toString() + "->" + (bitrate / (1000*1024)).toString(); // + " -> " + "8";
                    } else {
                        Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT)
                                .show()
                    }
                } else {
                    spinner.isClickable = true
                    spinner.isFocusable = true
                    button!!.setText(R.string.start_button)
                    rtspServerCamera1!!.stopStream()
                    tv_url.text = ""
                    tv_fps.text = ""
                    tv_resolution.text = ""
                }

            /*R.id.switch_camera -> try {
              rtspServerCamera1!!.switchCamera()
            } catch (e: CameraOpenException) {
              Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

            R.id.b_record -> if (!rtspServerCamera1!!.isRecording) {
              try {
                if (!folder.exists()) {
                  folder.mkdir()
                }
                val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                currentDateAndTime = sdf.format(Date())
                if (!rtspServerCamera1!!.isStreaming) {
                  if (rtspServerCamera1!!.prepareAudio() && rtspServerCamera1!!.prepareVideo()) {
                    rtspServerCamera1!!.startRecord(
                      folder.absolutePath + "/" + currentDateAndTime + ".mp4")
                    bRecord!!.setText(R.string.stop_record)
                    Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show()
                  } else {
                    Toast.makeText(this, "Error preparing stream, This device cant do it",
                      Toast.LENGTH_SHORT).show()
                  }
                } else {
                  rtspServerCamera1!!.startRecord(folder.absolutePath + "/" + currentDateAndTime + ".mp4")
                  bRecord!!.setText(R.string.stop_record)
                  Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show()
                }
              } catch (e: IOException) {
                rtspServerCamera1!!.stopRecord()
                bRecord!!.setText(R.string.start_record)
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
              }
            } else {
              rtspServerCamera1!!.stopRecord()
              bRecord!!.setText(R.string.start_record)
              Toast.makeText(this, "file " + currentDateAndTime + ".mp4 saved in " + folder.absolutePath,
                Toast.LENGTH_SHORT).show()
            }*/
            else -> {
            }
        }
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        rtspServerCamera1!!.startPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        if (rtspServerCamera1!!.isRecording) {
            rtspServerCamera1!!.stopRecord()
            bRecord!!.setText(R.string.start_record)
            Toast.makeText(this, "file " + currentDateAndTime + ".mp4 saved in " + folder.absolutePath,
                    Toast.LENGTH_SHORT).show()
            currentDateAndTime = ""
        }
        if (rtspServerCamera1!!.isStreaming) {
            rtspServerCamera1!!.stopStream()
            button!!.text = resources.getString(R.string.start_button)
            tv_url.text = ""
            tv_fps.text = ""
            tv_resolution.text = ""
        }
        rtspServerCamera1!!.stopPreview()
    }
}