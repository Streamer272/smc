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
import dev.svitan.smc.ui.views.AppViewModel
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
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

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
    private val viewModel = AppViewModel()

    companion object {
        private const val TAG = "MainActivity"
    }


    init {
        GlobalScope.launch {
            val result = runCatching {
                client.webSocket(method = HttpMethod.Get, host = "2.tcp.eu.ngrok.io", port = 19641, path = "/ws") {
                    viewModel.setConnected(ConnectionState.Connected)

                    val sendJob = launch {
                        viewModel.pressedFlow.collect {
                            Log.i(TAG, "Sending pressed $it")
                            send(if (it) "1" else "0")
                        }
                    }
                    val receiveJob = launch {
                        while (true) {
                            val message = incoming.receive()
                            if (message !is Frame.Text) continue

                            val pressed = message.readText() == "1"
                            Log.i(TAG, "Received pressed $pressed")

                            if (viewModel.vibratingFlow.value != pressed) {
                                viewModel.setVibrating(pressed)
                            }
                        }
                    }

                    viewModel.connectedFlow.takeWhile { it != ConnectionState.Closed }.collect {}
                    sendJob.cancel()
                    receiveJob.cancel()
                    close(CloseReason(CloseReason.Codes.NORMAL, "adios"))
                }
            }

            if (result.isFailure) {
                viewModel.setConnected(ConnectionState.Error)
                Log.e(TAG, result.exceptionOrNull().toString())
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            SMCTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen() }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.i(TAG, "Pressed down")
                viewModel.setPressed(false)
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.i(TAG, "Pressed up")
                viewModel.setPressed(true)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        viewModel.setConnected(ConnectionState.Closed)
        client.close()

        super.onDestroy()
    }
}
