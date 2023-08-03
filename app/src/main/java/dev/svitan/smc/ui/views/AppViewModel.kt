package dev.svitan.smc.ui.views

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancel

enum class ConnectionState {
    Connecting,
    Connected,
    Error,
    Closed,
}

class AppViewModel : ViewModel() {
    private val _pressedFlow = MutableStateFlow(false)
    val pressedFlow: StateFlow<Boolean> get() = _pressedFlow

    private val _connectedFlow = MutableStateFlow(ConnectionState.Connecting)
    val connectedFlow: StateFlow<ConnectionState> get() = _connectedFlow

    private val _vibratingFlow = MutableStateFlow(false)
    val vibratingFlow: StateFlow<Boolean> get() = _vibratingFlow

    fun setPressed(value: Boolean) {
        _pressedFlow.value = value
    }

    fun setConnected(value: ConnectionState) {
        _connectedFlow.value = value
    }

    fun setVibrating(value: Boolean) {
        _vibratingFlow.value = value
    }
}
