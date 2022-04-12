package com.algonics.rtmp.rtmp.message

import com.algonics.rtmp.flv.FlvPacket
import com.algonics.rtmp.rtmp.chunk.ChunkStreamId
import com.algonics.rtmp.rtmp.chunk.ChunkType
import java.io.InputStream

/**
 * Created by pedro on 21/04/21.
 */
class Audio(private val flvPacket: FlvPacket = FlvPacket(), streamId: Int = 0): RtmpMessage(BasicHeader(ChunkType.TYPE_0, ChunkStreamId.AUDIO)) {

  init {
    header.messageStreamId = streamId
    header.timeStamp = flvPacket.timeStamp.toInt()
    header.messageLength = flvPacket.length
  }

  override fun readBody(input: InputStream) {
  }

  override fun storeBody(): ByteArray = flvPacket.buffer

  override fun getType(): MessageType = MessageType.AUDIO

  override fun getSize(): Int = flvPacket.length

  override fun toString(): String {
    return "Audio, size: ${getSize()}"
  }
}