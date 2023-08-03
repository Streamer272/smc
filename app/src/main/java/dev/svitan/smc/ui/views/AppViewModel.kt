package dev.svitan.smc.ui.views

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ConnectionState {
    Connecting,
    Connected,
    Error,
    Closed,
}

class AppViewModel : ViewModel() {
    private val _pressedFlow = MutableStateFlow(false)
    val pressedFlow = _pressedFlow.asStateFlow()

    private val _connectedFlow = MutableStateFlow(ConnectionState.Connecting)
    val connectedFlow = _connectedFlow.asStateFlow()

    private val _vibratingFlow = MutableStateFlow(false)
    val vibratingFlow = _vibratingFlow.asStateFlow()

    init {
        viewModelScope.launch {
            Log.d("AppViewModel", "Observing connected")
            connectedFlow.collect {
                Log.d("AppViewModel", "Connected changed to $it")
            }
        }
    }

    fun setPressed(value: Boolean) {
        _pressedFlow.value = value
    }

    fun setConnected(value: ConnectionState) {
        Log.i("AppViewModel", "Setting connected to $value")
        _connectedFlow.update { value }
//        _connectedFlow.value = value
    }

    fun setVibrating(value: Boolean) {
        _vibratingFlow.value = value
    }
}
