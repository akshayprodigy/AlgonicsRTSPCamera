package com.algonics.rtpstreamer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algonics.encoder.input.decoder.AudioDecoderInterface;
import com.algonics.encoder.input.decoder.VideoDecoderInterface;
import com.algonics.rtpstreamer.rtspserver.RtspServerFileTransfer;
import com.algonics.rtpstreamer.rtspserver.RtspServerFromFile;
import com.algonics.rtpstreamer.utils.PathUtils;
import com.algonics.rtsp.rtsp.VideoCodec;
import com.algonics.rtsp.utils.ConnectCheckerRtsp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RTSPFileTransferActivity extends baseActivity implements ConnectCheckerRtsp, VideoDecoderInterface,AudioDecoderInterface {
    private TextView etUrl;
    private TextView tvFile;
    private String filePath = "";
    private RtspServerFromFile rtspServerFileTransfer = null;// RtspServerFromFile RtspServerFileTransfer
    boolean fromActivityResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtspfile_transfer);
        etUrl = findViewById(R.id.file_et_rtp_url);
        etUrl.setHint(R.string.hint_rtsp);
        tvFile = findViewById(R.id.file_tv_file);
        rtspServerFileTransfer = new RtspServerFromFile(getApplicationContext(),this,1935,this,this);
        //rtspServerFileTransfer = new RtspServerFileTransfer(this,this,this,1935);
        rtspServerFileTransfer.setVideoCodec(VideoCodec.H264);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!fromActivityResult)
            openGetFileIntent();
        fromActivityResult = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && data != null) {
            filePath = PathUtils.getPath(this, data.getData());
            Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
            if(tvFile == null)
                tvFile = findViewById(R.id.file_tv_file);
            tvFile.setText(filePath);
            fromActivityResult = true;
        }
    }

    public void GetVideoFile(View view) {
        openGetFileIntent();
    }

    public void ResyncToServer(View view) {
    }

    public void StartStreaming(View view) {
        try{
            if(prepare()){
                //File Video prepared
                Log.e("RTSP","StartStreaming prepare");
                rtspServerFileTransfer.startStream();
                etUrl.setText(rtspServerFileTransfer.getEndPointConnection());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean prepare() throws IOException {
        int bitRate = 8 * 1000 * 1024;
        int rotation = 0;
        boolean result = rtspServerFileTransfer.prepareVideo(filePath);//,bitRate,rotation
        result |= rtspServerFileTransfer.prepareAudio(filePath);
        return result;
    }

    public void openGetFileIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 5);
    }

    @Override
    public void onConnectionStartedRtsp(@NotNull String rtspUrl) {

    }

    @Override
    public void onConnectionSuccessRtsp() {

    }

    @Override
    public void onConnectionFailedRtsp(@NotNull String reason) {

    }

    @Override
    public void onNewBitrateRtsp(long bitrate) {

    }

    @Override
    public void onDisconnectRtsp() {

    }

    @Override
    public void onAuthErrorRtsp() {

    }

    @Override
    public void onAuthSuccessRtsp() {

    }

    @Override
    public void onAudioDecoderFinished() {

    }

    @Override
    public void onVideoDecoderFinished() {

    }
}