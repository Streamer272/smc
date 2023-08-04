package dev.svitan.smc

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.svitan.smc.ui.screens.HomeScreen
import dev.svitan.smc.ui.theme.SMCTheme
import dev.svitan.smc.ui.views.ConnectionState

class MainActivity : ComponentActivity() {
    private var setConnection: (ConnectionState) -> Unit = {}
    private var setPressed: (Boolean) -> Unit = {}

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            SMCTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.d(TAG, "Pressed down")
                setPressed(true)
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.d(TAG, "Pressed up")
                setPressed(true)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        setConnection(ConnectionState.Closed)
        super.onDestroy()
    }
}
