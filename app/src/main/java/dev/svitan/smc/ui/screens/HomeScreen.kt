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
import dev.svitan.smc.ui.views.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val viewModel = remember { AppViewModel() }
    val scope = rememberCoroutineScope()
    val connected by viewModel.connectedFlow.collectAsState(scope)

    LaunchedEffect(viewModel.connectedFlow) {
        viewModel.connectedFlow.collect {
            Log.i("HomeScreen", "!!!!!!!!!!!!!!!!!!!!!! $it !!!!!!!!!!!!!!!!!!!")
        }
    }

    val another = AppViewModel()
    LaunchedEffect(another) {
        another.connectedFlow.collect {
            Log.i("HomeScreen", "<<<<<<<<<<<<<<<< $it >>>>>>>>>>>>>>")
        }
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
                text = "$connected / ${viewModel.connectedFlow.collectAsState()}",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
