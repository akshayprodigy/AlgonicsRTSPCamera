package com.algonics.rtmp.rtmp.chunk

/**
 * Created by pedro on 21/04/21.
 */
enum class ChunkStreamId(val mark: Byte) {
  PROTOCOL_CONTROL(0x02),
  OVER_CONNECTION(0x03),
  OVER_CONNECTION2(0x04),
  OVER_STREAM(0x05),
  VIDEO(0x06),
  AUDIO(0x07)
}