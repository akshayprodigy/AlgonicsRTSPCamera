package com.algonics.rtpstreamer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FileTransferDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_transfer_demo)
    }

    fun GetVideoFile(view: View) {}
    fun ResyncToServer(view: View) {}
    fun StartStreaming(view: View) {}
}