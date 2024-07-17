package com.hanas.addy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hanas.addy.home.GoBack
import com.hanas.addy.home.NavScreen
import com.hanas.addy.home.NavigationHandler
import com.hanas.addy.home.cardStackListComposable
import com.hanas.addy.home.createNewCardStackNavigation
import com.hanas.addy.home.homeComposable
import com.hanas.addy.login.Login
import com.hanas.addy.login.loginComposable
import com.hanas.addy.ui.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                AppTheme {
                    val navController = rememberNavController()
                    val navigate = NavigationHandler { action, _ ->
                        when (action) {
                            is NavScreen -> navController.navigate(action)
                            is GoBack -> navController.popBackStack()
                        }
                    }
                    Surface(color = Color.Black) {
                        NavHost(
                            navController,
                            Login,
                            enterTransition = { slideIntoContainer(Start, tween(300)) },
                            exitTransition = { fadeOut(tween(300, 100, easing = FastOutSlowInEasing), 0.5f) },
                            popExitTransition = { slideOutOfContainer(End, tween(300)) },
                            popEnterTransition = { fadeIn(tween(300, easing = LinearEasing), 0.5f) },
                        ) {
                            loginComposable(navigate)
                            homeComposable(navigate)
                            cardStackListComposable(navigate)
                            createNewCardStackNavigation(navigate, navController)
                        }
                    }
                }
        }
    }
}