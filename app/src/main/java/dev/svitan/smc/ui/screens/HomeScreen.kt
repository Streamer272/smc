package dev.svitan.smc.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.svitan.smc.R
import dev.svitan.smc.ui.components.SMCScaffold
import dev.svitan.smc.ui.views.ConnectionState
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun HomeScreen() {
    var connection by remember { mutableStateOf(ConnectionState.Connecting) }
    var pressed by remember { mutableStateOf(false) }
    var vibrating by remember { mutableStateOf(false) }
    var onPressedChanged by remember { mutableStateOf({}) }
    var onDisconnected by remember { mutableStateOf({}) }
    val scope = rememberCoroutineScope()
    val TAG = "HomeScreen"


    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets) {
            pingInterval = 15_000
        }
    }

    val result = runCatching {
        Log.d(TAG, "Connecting")
        scope.launch {
            client.webSocket(method = HttpMethod.Get, host = "7.tcp.eu.ngrok.io", port = 17631, path = "/ws") {
                Log.d(TAG, "Connected")
                connection = ConnectionState.Connected

                val sendJob = launch {
                    onPressedChanged = {
                        Log.i(TAG, "Sending pressed $pressed")
                        runBlocking { send(if (pressed) "1" else "0") }
                    }
                }
                val receiveJob = launch {
                    while (true) {
                        val message = incoming.receive()
                        if (message !is Frame.Text) continue

                        val received = message.readText() == "1"
                        Log.i(TAG, "Received pressed $received")

                        vibrating = received
                    }
                }

                onDisconnected = {
                    sendJob.cancel()
                    receiveJob.cancel()
                    runBlocking { close(CloseReason(CloseReason.Codes.NORMAL, "adios")) }
                }
            }
        }
    }

    Log.d(TAG, "After connect with result (isFailure) ${result.isFailure}")
    if (result.isFailure) {
        connection = ConnectionState.Error
        Log.i(TAG, "Couldn't connect (cause: ${result.exceptionOrNull().toString()})")
    }

    SMCScaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.welcome), fontSize = 22.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$connection",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
