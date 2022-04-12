package com.algonics.rtpstreamer.rtspserver;

import android.content.Context;
import android.media.MediaCodec;
import android.util.Log;

import androidx.annotation.Nullable;

import com.algonics.encoder.input.decoder.AudioDecoderInterface;
import com.algonics.encoder.input.decoder.VideoDecoderInterface;
import com.algonics.encoder.utils.CodecUtil;
import com.algonics.rtplibrary.base.FromFileBase;
import com.algonics.rtsp.rtsp.RtspClient;
import com.algonics.rtsp.rtsp.VideoCodec;
import com.algonics.rtsp.utils.ConnectCheckerRtsp;

import java.nio.ByteBuffer;

    public class RtspServerFileTransfer extends FromFileBase  {

    public RtspServer rtspServer;

    public RtspServerFileTransfer(ConnectCheckerRtsp connectCheckerRtsp,
                        VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface, int port) {
        super(videoDecoderInterface, audioDecoderInterface);
        rtspServer = new RtspServer(connectCheckerRtsp, port);
        //rtspClient = new RtspClient(connectCheckerRtsp);
    }

//    public RtspServerFileTransfer(Context context, VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
//        super(context, videoDecoderInterface, audioDecoderInterface);
//    }

    public  void setVideoCodec(VideoCodec videoCodec){
         if (videoCodec == VideoCodec.H265){
             videoEncoder.setType(CodecUtil.H265_MIME);
         } else {
             videoEncoder.setType(CodecUtil.H264_MIME);
         }
    }

    public String getEndPointConnection(){
        Log.e("RTSP ","getEndPointConnection serverIp: "+rtspServer.getRtspServerIp()+" rtspServer.port: "+rtspServer.getRtspServerPort());
        return "rtsp://" + rtspServer.getRtspServerIp() + ":" + rtspServer.getRtspServerPort() + "/";
    }

    public void startStream(){
        super.startStream("");
    }
    @Override
    public void setAuthorization(String user, String password) {
        rtspServer.setAuth(user, password);
    }

    @Override
    protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
        rtspServer.setRTSPStereo(isStereo);
        rtspServer.setRTSPSampleRate(sampleRate);
    }

    @Override
    protected void startStreamRtp(String url) {

    }

    @Override
    protected void stopStreamRtp() {
        rtspServer.stopServer();
    }

    @Override
    protected boolean shouldRetry(String reason) {
        return false;
    }

    @Override
    public void setReTries(int reTries) {

    }

    @Override
    protected void reConnect(long delay, @Nullable @org.jetbrains.annotations.Nullable String backupUrl) {

    }

    @Override
    public boolean hasCongestion() {
        return rtspServer.hasCongestion().congetionDataList.get(0).hasCongestion;
    }

    @Override
    public void resizeCache(int newSize) throws RuntimeException {

    }

    @Override
    public int getCacheSize() {
        return 0;
    }

    @Override
    public long getSentAudioFrames() {
        return 0;
    }

    @Override
    public long getSentVideoFrames() {
        return 0;
    }

    @Override
    public long getDroppedAudioFrames() {
        return 0;
    }

    @Override
    public long getDroppedVideoFrames() {
        return 0;
    }

    @Override
    public void resetSentAudioFrames() {

    }

    @Override
    public void resetSentVideoFrames() {

    }

    @Override
    public void resetDroppedAudioFrames() {

    }

    @Override
    public void resetDroppedVideoFrames() {

    }

    @Override
    protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
        ByteBuffer newSps = sps.duplicate();
        ByteBuffer newPps = pps.duplicate();
        ByteBuffer newVps = vps != null ? vps.duplicate() : null;
        rtspServer.setVideoInfo(newSps, newPps, newVps);
        rtspServer.startServer();
    }

    @Override
    protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
        rtspServer.sendVideo(h264Buffer, info);
    }

    @Override
    protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
        rtspServer.sendAudio(aacBuffer, info);
    }

    @Override
    public void setLogs(boolean enable) {
        rtspServer.setLogs(enable);
    }
}
