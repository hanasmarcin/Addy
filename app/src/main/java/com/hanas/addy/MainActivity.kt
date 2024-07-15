package com.hanas.addy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hanas.addy.home.GoBack
import com.hanas.addy.home.Home
import com.hanas.addy.home.NavScreen
import com.hanas.addy.home.Navigate
import com.hanas.addy.home.addNewCardStackComposable
import com.hanas.addy.home.cardStackListComposable
import com.hanas.addy.home.homeComposable
import com.hanas.addy.ui.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                val navigate: Navigate = { action ->
                    when (action) {
                        is NavScreen -> navController.navigate(action)
                        is GoBack -> navController.popBackStack()
                    }
                }
                NavHost(
                    navController,
                    Home,
                    enterTransition = { slideIntoContainer(Start, tween(200)) },
                    exitTransition = { ExitTransition.None },
                    popExitTransition = { slideOutOfContainer(End, tween(200)) },
                    popEnterTransition = { EnterTransition.None },
                ) {
                    homeComposable(navigate)
                    cardStackListComposable(navigate)
                    addNewCardStackComposable()
                }
            }
        }
    }
}