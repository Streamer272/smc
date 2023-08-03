package dev.svitan.smc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.svitan.smc.R
import dev.svitan.smc.ui.components.SMCScaffold
import dev.svitan.smc.ui.views.AppViewModel
import dev.svitan.smc.ui.views.ConnectionState

@Composable
fun HomeScreen() {
    val viewModel = AppViewModel()
    val connected = viewModel.connectedFlow.collectAsState()

    SMCScaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.welcome), fontSize = 22.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    when (connected.value) {
                        ConnectionState.Connecting -> R.string.connecting
                        ConnectionState.Connected -> R.string.connected
                        ConnectionState.Error -> R.string.connectionError
                        ConnectionState.Closed -> R.string.connectionClosed
                    }
                ),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
