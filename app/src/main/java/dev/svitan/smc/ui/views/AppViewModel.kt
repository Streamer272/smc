package dev.svitan.smc.ui.views

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel : ViewModel() {
    private val _pressedFlow = MutableStateFlow(false)
    val pressedFlow: StateFlow<Boolean> get() = _pressedFlow

    private val _connectedFlow = MutableStateFlow(false)
    val connectedFlow: StateFlow<Boolean> get() = _connectedFlow

    fun setPressed(value: Boolean) {
        _pressedFlow.value = value
    }

    fun setConnected(value: Boolean) {
        _connectedFlow.value = value
    }
}
