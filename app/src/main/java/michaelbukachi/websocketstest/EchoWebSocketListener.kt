package michaelbukachi.websocketstest

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.decodeHex

class EchoWebSocketListener(val connection: Connection) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send("Hello, it's SSaurel !")
        webSocket.send("What's up ?")
        webSocket.send("deadbeef".decodeHex())
        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i("Bukachi", "Receiving : $text")
        connection.output(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.i("Bukachi", "Receiving bytes : " + bytes.hex())
        connection.output(bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        Log.i("Bukachi", "Closing : $code / $reason")
        connection.output(reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.i("Bukachi", "Error : " + t.message)
        connection.output(t.message.toString())
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}