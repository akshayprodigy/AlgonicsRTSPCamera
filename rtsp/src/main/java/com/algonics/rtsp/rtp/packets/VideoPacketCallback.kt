package com.algonics.rtsp.rtp.packets

import com.algonics.rtsp.rtsp.RtpFrame

/**
 * Created by pedro on 7/11/18.
 */
interface VideoPacketCallback {
  fun onVideoFrameCreated(rtpFrame: RtpFrame)
}