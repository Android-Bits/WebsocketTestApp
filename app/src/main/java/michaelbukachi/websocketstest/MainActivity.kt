package michaelbukachi.websocketstest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tinder.scarlet.Event
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.State
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.channels.ReceiveChannel
import okhttp3.OkHttpClient
import okhttp3.Request


interface EchoService {
    @Receive
    fun observeState(): ReceiveChannel<State>

    @Receive
    fun observeEvent(): ReceiveChannel<Event>

    @Send
    fun sendText(message: String): Boolean
}

class MainActivity : AppCompatActivity(), Connection {

    val client = OkHttpClient()
    lateinit var scarletInstance: Scarlet
    lateinit var echoService: EchoService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start()

//        scarletInstance = Scarlet.Builder()
//            .webSocketFactory(client.newWebSocketFactory("wss://echo.websocket.org"))
//            .addMessageAdapterFactory(MoshiMessageAdapter.Factory())
//            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
//            .build()
//        echoService = scarletInstance.create()
//        echoService.observeState().receiveAsFlow().onEach {
//            when (it) {
//                is State.Connected -> {
//                    state.text = "Connected"
//                }
//                is State.Disconnected -> {
//                    state.text = "Disconnected"
//                }
//                is State.Destroyed -> {
//                    state.text = "Destroyed"
//                }
//                is State.WaitingToRetry -> {
//                    state.text = "Retrying"
//                }
//            }
//        }.launchIn(lifecycleScope)
    }


    private fun start() {
        val request: Request = Request.Builder().url("wss://echo.websocket.org").build()
        val listener = EchoWebSocketListener(this)
        val ws = client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
    }

    override fun output(message: String) {
        runOnUiThread {
            state.text = message
        }
    }


}