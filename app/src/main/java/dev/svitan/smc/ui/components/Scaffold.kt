package dev.svitan.smc.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SMCScaffold(content: @Composable (PaddingValues) -> Unit) {
    Scaffold { contentPadding ->
        content(contentPadding)
    }
}
