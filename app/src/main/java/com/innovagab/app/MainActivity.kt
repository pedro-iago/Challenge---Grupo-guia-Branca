package com.innovagab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.innovagab.app.navigation.AppNavigation
import com.innovagab.app.ui.theme.InnovaGABTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InnovaGABTheme {
                AppNavigation()
            }
        }
    }
}
