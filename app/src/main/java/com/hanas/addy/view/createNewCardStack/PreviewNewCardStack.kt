package com.hanas.addy.view.createNewCardStack

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.PlayingCardStack
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.CardStackPager
import com.hanas.addy.ui.GoBack
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.samplePlayingCard
import com.hanas.addy.view.home.NavigationHandler
import org.koin.androidx.compose.navigation.koinNavViewModel

fun NavGraphBuilder.viewNewCardStackComposable(navHandler: NavigationHandler, navController: NavController) {
    composable<CreateNewCardStack.PreviewNewStack> {
        val parent = navController.getBackStackEntry<CreateNewCardStack>()
        val viewModel: CreateNewCardStackViewModel = koinNavViewModel(viewModelStoreOwner = parent)
        val cards by viewModel.cardStackFlow.collectAsState()
        ViewNewCardStack(navHandler, cards.data, viewModel::deleteGeneratedStack)
    }
}

@Composable
private fun ViewNewCardStack(
    navHandler: NavigationHandler,
    stack: PlayingCardStack?,
    deleteGeneratedStack: () -> Unit
) {
    BackHandler {
        deleteGeneratedStack()
        navHandler.navigate(GoBack)
    }
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = {
            Text(stack?.title.orEmpty())
        }
    ) {
        CardStackPager(stack?.cards.orEmpty())
    }
}

@Preview
@Composable
fun ViewNewCardStackScreenPreview() {

    AppTheme {
        val list = PlayingCardStack(
            cards = listOf(
                samplePlayingCard,
                samplePlayingCard
            )
        )
        ViewNewCardStack({ }, list, {})
    }
}