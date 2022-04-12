package com.algonics.rtpstreamer.rtspserver

interface ClientListener {
    fun onDisconnected(client: ServerClient)
}