package com.algonics.rtmp.rtmp.message.command

import com.algonics.rtmp.rtmp.chunk.ChunkStreamId
import com.algonics.rtmp.rtmp.chunk.ChunkType
import com.algonics.rtmp.rtmp.message.BasicHeader
import com.algonics.rtmp.rtmp.message.MessageType

/**
 * Created by pedro on 21/04/21.
 */
class CommandAmf3(name: String = "", commandId: Int = 0, timestamp: Int = 0, streamId: Int = 0, basicHeader: BasicHeader =
    BasicHeader(ChunkType.TYPE_0, ChunkStreamId.OVER_CONNECTION)): Command(name, commandId, timestamp, streamId, basicHeader = basicHeader) {
  override fun getType(): MessageType = MessageType.COMMAND_AMF3
}