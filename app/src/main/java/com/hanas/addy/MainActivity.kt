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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.cardStackDetail.CardStackDetail
import com.hanas.addy.view.cardStackDetail.cardStackDetailComposable
import com.hanas.addy.view.cardStackList.CardStackList
import com.hanas.addy.view.cardStackList.cardStackListComposable
import com.hanas.addy.view.createNewCardStack.CreateNewCardStack
import com.hanas.addy.view.createNewCardStack.createNewCardStackNavigation
import com.hanas.addy.view.gameSession.chooseGameSession.ChooseCardStack
import com.hanas.addy.view.gameSession.chooseGameSession.ChooseSession
import com.hanas.addy.view.gameSession.chooseGameSession.chooseCardStackComposable
import com.hanas.addy.view.gameSession.chooseGameSession.chooseGameSessionComposable
import com.hanas.addy.view.gameSession.createNewSession.createNewSessionComposable
import com.hanas.addy.view.home.Home
import com.hanas.addy.view.home.homeComposable
import com.hanas.addy.view.login.Login
import com.hanas.addy.view.login.loginComposable
import com.hanas.addy.view.playTable.playTableComposable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                Surface(color = Color.Black) {
                    NavHost(
                        navController,
                        if (Firebase.auth.currentUser == null) Login else Home,
                        enterTransition = { slideIntoContainer(Start, tween(300)) },
                        exitTransition = {
                            fadeOut(tween(300, 100, easing = FastOutSlowInEasing), 0.5f)
                        },
                        popExitTransition = { slideOutOfContainer(End, tween(300)) },
                        popEnterTransition = { fadeIn(tween(300, easing = LinearEasing), 0.5f) },
                    ) {
                        loginComposable(navController)
                        homeComposable(
                            openPlayScreen = {
                                navController.navigate(ChooseSession)
                            },
                            openCardStackList = {
                                navController.navigate(CardStackList)
                            },
                            navigateBackToLogin = {
                                navController.navigate(Login) {
                                    popUpTo(Home) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            })
                        cardStackListComposable(
                            openCreateNewCardStack = {
                                navController.navigate(CreateNewCardStack)
                            },
                            openCardStackDetail = {
                                navController.navigate(CardStackDetail(it))
                            },
                            navigateBack = {
                                navController.navigateUp()
                            }
                        )
                        cardStackDetailComposable(
                            navigateBack = {
                                navController.navigateUp()
                            }
                        )
                        createNewCardStackNavigation(
                            navController = navController,
                            navigateBack = {
                                navController.navigateUp()
                            })
                        playTableComposable(
                            navigateBack = {
                                navController.navigateUp()
                            },
                        )
                        chooseGameSessionComposable(
                            navController = navController,
                            navigateBack = {
                                navController.navigateUp()
                            },
                            openChooseCardStack = {
                                navController.navigate(ChooseCardStack)
                            }
                        )
                        createNewSessionComposable(
                            navController = navController,
                            navigateBack = navController::navigateUp
                        )
                        chooseCardStackComposable(
                            navController = navController,
                            navigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}