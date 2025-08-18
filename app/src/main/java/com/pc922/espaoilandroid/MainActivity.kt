package com.pc922.espaoilandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pc922.espaoilandroid.ui.navigation.AppScaffold
import com.pc922.espaoilandroid.ui.theme.EspaoilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EspaoilTheme {
                AppScaffold()
            }
        }
    }
}