package com.hanas.addy.view.gameSession.chooseGameSession

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.cardStackList.CardStacksListContent
import com.hanas.addy.view.cardStackList.SamplePlayCardStackListProvider
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object ChooseCardStack : NavScreen

fun NavGraphBuilder.chooseCardStackComposable(navHandler: NavigationHandler) {
    composable<ChooseCardStack> {
        val viewModel: ChooseCardStackViewModel = koinNavViewModel()
        val state by viewModel.cardStacksFlow.collectAsState()
        viewModel.observeNavigation(navHandler)
        ChooseCardStackScreen(state, navHandler, viewModel::onChooseCardStack)
    }
}

@Composable
fun ChooseCardStackScreen(
    cardStacks: List<PlayCardStack>,
    navHandler: NavigationHandler,
    onChooseCardStack: (String) -> Unit
) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { Text("Choose Card Stack") }
    ) {
        CardStacksListContent(cardStacks) { id ->
            onChooseCardStack(id)
        }
    }
}

@Preview
@Composable
fun ChooseCardStackScreenPreview(@PreviewParameter(SamplePlayCardStackListProvider::class) cardStacks: List<PlayCardStack>) {
    AppTheme {
        ChooseCardStackScreen(cardStacks, {}) {}
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun NavigationRequester.observeNavigation(navHandler: NavigationHandler) {
    val navigationRequest by navigationRequestFlow.collectAsState(initial = null)
    LaunchedEffect(navigationRequest) {
        navigationRequest?.let {
            navHandler.navigate(it)
        }
    }
}
