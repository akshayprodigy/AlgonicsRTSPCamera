package com.algonics.rtmp.flv.audio

import com.algonics.rtmp.flv.FlvPacket

/**
 * Created by pedro on 29/04/21.
 */
interface AudioPacketCallback {
  fun onAudioFrameCreated(flvPacket: FlvPacket)
}