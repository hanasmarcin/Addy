package com.hanas.addy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hanas.addy.home.Home
import com.hanas.addy.home.homeComposable
import com.hanas.addy.ui.theme.AddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddyTheme {
                val navController = rememberNavController()
                NavHost(navController, Home) {
                    homeComposable()
                }
            }
        }
    }
}