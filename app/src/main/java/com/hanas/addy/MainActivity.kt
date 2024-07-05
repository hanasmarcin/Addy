@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.hanas.addy.home.HomeScreen
import com.hanas.addy.ui.theme.AddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddyTheme {
                // A surface container using the 'background' color from the theme
                HomeScreen()
            }
        }
    }
}