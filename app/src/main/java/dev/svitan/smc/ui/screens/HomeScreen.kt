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
import dev.svitan.smc.ui.views.AppViewModel
import dev.svitan.smc.R

@Composable
fun HomeScreen() {
    val viewModel = AppViewModel()
    val connected = viewModel.connectedFlow.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.welcome), fontSize = 22.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(if (connected.value) R.string.connected else R.string.connecting),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}
