package dev.svitan.smc

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.svitan.smc.ui.theme.SMCTheme
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    private val client: HttpClient = HttpClient(CIO) {
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.INFO
//        }
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets) {
            pingInterval = 15_000
        }
    }
    private var connected = false


    init {
        GlobalScope.launch {
            try {
                client.webSocket(method = HttpMethod.Get, host = "5.tcp.eu.ngrok.io", port = 14956, path = "/ws") {
                    connected = true
                    send("Hello World!")

                    Log.i("MainActivity", (incoming.receive() as Frame.Text).readText())
                    close(CloseReason(CloseReason.Codes.NORMAL, "adios"))
                }
            } catch (err: Exception) {
                Log.e("MainActivity", err.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SMCTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.welcome), fontSize = 22.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(if (connected) R.string.connected else R.string.connecting),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.i("MainActivity", "Pressed down")
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.i("MainActivity", "Pressed up")
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        client.close()

        super.onDestroy()
    }
}
