package dev.svitan.smc.ui.screens

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

@Composable
fun HomeScreen(
    onPressedUpdate: (Boolean) -> Unit,
    onConnectionUpdate: (ConnectionState) -> Unit,
    setSetConnection: ((ConnectionState) -> Unit) -> Unit,
    setSetPressed: ((Boolean) -> Unit) -> Unit,
    setSetVibrating: ((Boolean) -> Unit) -> Unit,
    connect: () -> Unit
) {
    var connection by remember { mutableStateOf(ConnectionState.Connecting) }
    var pressed by remember { mutableStateOf(false) }
    var vibrating by remember { mutableStateOf(false) }

    setSetConnection { connection = it }
    setSetPressed { pressed = it }
    setSetVibrating { vibrating = it }

    LaunchedEffect(pressed) {
        onPressedUpdate(pressed)
    }
    LaunchedEffect(connection) {
        onConnectionUpdate(connection)
    }

    connect()

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
