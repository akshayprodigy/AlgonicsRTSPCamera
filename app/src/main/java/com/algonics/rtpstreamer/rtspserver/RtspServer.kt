package com.algonics.rtpstreamer.rtspserver


import android.media.MediaCodec
import android.util.Log
import com.algonics.rtplibrary.util.CongetionDataOfAllClients
import com.algonics.rtsp.utils.ConnectCheckerRtsp
import com.algonics.rtsp.utils.RtpConstants
import java.io.*
import java.net.*
import java.nio.ByteBuffer
import java.util.*


class RtspServer(private val connectCheckerRtsp: ConnectCheckerRtsp,
                 public val port: Int): ClientListener {

    private val TAG = "RtspServer"
    private var server: ServerSocket? = null
    public val serverIp = getIPAddress(true)
    var sps: ByteBuffer? = null
    var pps: ByteBuffer? = null
    var vps: ByteBuffer? = null
    var sampleRate = 2*32000//32000
    var isStereo = true
    private val clients = mutableListOf<ServerClient>()
    private var isOnlyAudio = false
    private var thread: Thread? = null
    private var user: String? = null
    private var password: String? = null

    fun setAuth(user: String?, password: String?) {
        this.user = user
        this.password = password
    }
    fun getRtspServerIp():String{
        return serverIp
    }

    fun setRTSPStereo(onlyStereo: Boolean){
        isStereo = onlyStereo;
    }

    fun setRTSPSampleRate(onlySample: Int){
        sampleRate = onlySample;
    }

    fun getRtspServerPort():Int{
        return port;
    }

    fun startServer() {
        Log.i(TAG, "Server start called")
        stopServer()
        thread = Thread {
            server = ServerSocket(port)
            while (!Thread.interrupted()) {
                Log.i(TAG, "Server started $serverIp:$port")
                try {
                    val client =
                            ServerClient(server!!.accept(), serverIp, port, connectCheckerRtsp, sps, pps, vps, sampleRate,
                                    isStereo, isOnlyAudio, user, password, this)
                    client.start()
                    synchronized(clients) {
                        clients.add(client)
                    }
                } catch (e: SocketException) {
                    Log.e(TAG, "SocketException Server Error", e)
                    break
                } catch (e: IOException) {
                    Log.e(TAG, e.message ?: "IOException")
                    continue
                } catch (e : Exception){
                    Log.e(TAG, "Exception Server Error", e)
                    continue
                }
            }
            Log.i(TAG, "Server finished")
        }
        thread?.start()
    }

    fun getNumClients(): Int = clients.size

    fun stopServer() {
        synchronized(clients) {
            clients.forEach { it.stopClient() }
            clients.clear()
        }
        thread?.interrupt()
        try {
            thread?.join(100)
        } catch (e: InterruptedException) {
            thread?.interrupt()
        }
        thread = null
        if (server != null) {
            if (!server!!.isClosed) server!!.close()
        }
    }

    fun setOnlyAudio(onlyAudio: Boolean) {
        if (onlyAudio) {
            RtpConstants.trackAudio = 0
            RtpConstants.trackVideo = 1
        } else {
            RtpConstants.trackVideo = 0
            RtpConstants.trackAudio = 1
        }
        this.isOnlyAudio = onlyAudio
    }

    fun setLogs(enable: Boolean) {
        synchronized(clients) {
            clients.forEach { it.rtspSender.setLogs(enable) }
        }
    }

    fun sendVideo(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        synchronized(clients) {
            clients.forEach {
                if (it.isAlive && it.canSend) {
                    it.rtspSender.sendVideoFrame(h264Buffer.duplicate(), info)
                }
            }
        }
    }

    fun sendAudio(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        synchronized(clients) {
            clients.forEach {
                if (it.isAlive && it.canSend) {
                    it.rtspSender.sendAudioFrame(aacBuffer.duplicate(), info)
                }
            }
        }
    }

    fun setVideoInfo(sps: ByteBuffer, pps: ByteBuffer, vps: ByteBuffer?) {
        this.sps = sps
        this.pps = pps
        this.vps = vps  //H264 has no vps so if not null assume H265
    }

    private fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> =
                        Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return "0.0.0.0"
    }

    fun hasCongestion(): CongetionDataOfAllClients {
        synchronized(clients) {
            var congetionList = CongetionDataOfAllClients()
            //var congestion = false
            clients.forEach {
                //if (it.hasCongestion()) congestion = true
                congetionList.congetionDataList.add(it.hasCongestion())
            }
            return congetionList
        }
    }

    override fun onDisconnected(client: ServerClient) {
        synchronized(clients) {
            client.stopClient()
            clients.remove(client)
        }
    }
}