package com.algonics.rtmp.rtmp.message.data

import com.algonics.rtmp.rtmp.chunk.ChunkStreamId
import com.algonics.rtmp.rtmp.chunk.ChunkType
import com.algonics.rtmp.rtmp.message.BasicHeader
import com.algonics.rtmp.rtmp.message.MessageType

/**
 * Created by pedro on 21/04/21.
 */
class DataAmf0(name: String = "", timeStamp: Int = 0, streamId: Int = 0, basicHeader: BasicHeader = BasicHeader(ChunkType.TYPE_0, ChunkStreamId.OVER_CONNECTION)):
    Data(name, timeStamp, streamId, basicHeader) {
  override fun getType(): MessageType = MessageType.DATA_AMF0
}