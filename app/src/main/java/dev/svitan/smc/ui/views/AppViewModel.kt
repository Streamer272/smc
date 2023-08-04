package dev.svitan.smc.ui.views

import androidx.lifecycle.ViewModel

enum class ConnectionState {
    Connecting,
    Connected,
    Error,
    Closed,
}

class AppViewModel : ViewModel()
