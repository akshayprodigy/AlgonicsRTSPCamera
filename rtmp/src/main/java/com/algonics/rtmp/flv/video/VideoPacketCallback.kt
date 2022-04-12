package com.algonics.rtmp.flv.video

import com.algonics.rtmp.flv.FlvPacket

/**
 * Created by pedro on 29/04/21.
 */
interface VideoPacketCallback {
  fun onVideoFrameCreated(flvPacket: FlvPacket)
}