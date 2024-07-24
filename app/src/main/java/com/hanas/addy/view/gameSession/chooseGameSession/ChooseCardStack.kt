package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable

@Serializable
object ChooseCardStack : NavScreen

fun NavGraphBuilder.chooseCardStackComposable(navHandler: NavigationHandler) {
    composable<ChooseCardStack> {
        ChooseCardStackScreen(navHandler)
    }
}

@Composable
fun ChooseCardStackScreen(navHandler: NavigationHandler) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { Text("Choose Card Stack") }
    ) {

    }
}

@Preview
@Composable
fun ChooseCardStackScreenPreview() {
    AppTheme {
        ChooseCardStackScreen {}
    }
}