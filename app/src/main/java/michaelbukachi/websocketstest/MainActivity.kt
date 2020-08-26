package michaelbukachi.websocketstest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.tinder.scarlet.Event
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.State
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.OkHttpClient
import okhttp3.WebSocket

interface EchoService {
    @Receive
    fun observeState(): ReceiveChannel<State>

    @Receive
    fun observeEvent(): ReceiveChannel<Event>

    @Send
    fun sendText(message: String): Boolean
}

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()
    lateinit var scarletInstance: Scarlet
    lateinit var echoService: EchoService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scarletInstance = Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory("ws://demos.kaazing.com/echo"))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()
        echoService = scarletInstance.create()
        echoService.observeState().receiveAsFlow().onEach {
            when (it) {
                is State.Connected -> {
                    state.text = "Connected"
                }
                is State.Disconnected -> {
                    state.text = "Disconnected"
                }
                is State.Destroyed -> {
                    state.text = "Destroyed"
                }
                is State.WaitingToRetry -> {
                    state.text = "Retrying"
                }
            }
        }.launchIn(lifecycleScope)
    }
}