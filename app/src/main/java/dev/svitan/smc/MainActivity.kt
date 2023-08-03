package dev.svitan.smc

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.svitan.smc.ui.screens.HomeScreen
import dev.svitan.smc.ui.theme.SMCTheme
import dev.svitan.smc.ui.views.ConnectionState
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets) {
            pingInterval = 15_000
        }
    }

    private val pressedChannel = Channel<Boolean>()
    private val connectionChannel = Channel<ConnectionState>()
    private var setConnection: (ConnectionState) -> Unit = {}
    private var setPressed: (Boolean) -> Unit = {}
    private var setVibrating: (Boolean) -> Unit = {}

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            SMCTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                {
                                    runBlocking {
                                        pressedChannel.send(it)
                                    }
                                },
                                {
                                    runBlocking {
                                        connectionChannel.send(it)
                                    }
                                },
                                { setConnection = it },
                                { setPressed = it },
                                { setVibrating = it },
                                ::connect
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.d(TAG, "Pressed down")
                setPressed(true)
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.d(TAG, "Pressed up")
                setPressed(true)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        setConnection(ConnectionState.Closed)
        client.close()
        super.onDestroy()
    }

    private fun connect() {
        GlobalScope.launch {
            val result = runCatching {
                Log.d(TAG, "Connecting")
                client.webSocket(method = HttpMethod.Get, host = "0.tcp.eu.ngrok.io", port = 16755, path = "/ws") {
                    Log.d(TAG, "Connected")
                    setConnection(ConnectionState.Connected)

                    val sendJob = launch {
                        while (!pressedChannel.isClosedForReceive) {
                            val pressed = pressedChannel.receive()
                            Log.i(TAG, "Sending pressed $pressed")
                            launch {
                                send(if (pressed) "1" else "0")
                            }
                        }
                    }
                    val receiveJob = launch {
                        while (true) {
                            val message = incoming.receive()
                            if (message !is Frame.Text) continue

                            val pressed = message.readText() == "1"
                            Log.i(TAG, "Received pressed $pressed")

                            setVibrating(pressed)
                        }
                    }

                    while (!connectionChannel.isClosedForReceive) {
                        if (connectionChannel.receive() == ConnectionState.Closed) break
                    }

                    sendJob.cancel()
                    receiveJob.cancel()
                    close(CloseReason(CloseReason.Codes.NORMAL, "adios"))
                }
            }

            if (result.isFailure) {
                setConnection(ConnectionState.Error)
                Log.e(TAG, result.exceptionOrNull().toString())
            }
        }
    }
}
