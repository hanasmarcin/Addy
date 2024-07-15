package com.hanas.addy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.home.Home
import com.hanas.addy.home.Navigate
import com.hanas.addy.home.addNewCardStackComposable
import com.hanas.addy.home.cardStackListComposable
import com.hanas.addy.home.homeComposable
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                val navigate: Navigate = {
                    navController.navigate(it)
                }
                NavHost(navController, Home) {
                    homeComposable(navigate)
                    cardStackListComposable()
                    addNewCardStackComposable()
                }
            }
        }
    }
}