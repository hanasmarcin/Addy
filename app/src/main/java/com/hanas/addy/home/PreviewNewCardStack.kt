package com.hanas.addy.home

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.components.AppScaffold
import org.koin.androidx.compose.navigation.koinNavViewModel

fun NavGraphBuilder.viewNewCardStackComposable(navHandler: NavigationHandler, navController: NavController) {
    composable<CreateNewCardStack.PreviewNewStack> {
        val parent = navController.getBackStackEntry<CreateNewCardStack>()
        val viewModel: CreateNewCardStackViewModel = koinNavViewModel(viewModelStoreOwner = parent)
        val cards by viewModel.outputContentFlow.collectAsState()
        ViewNewCardStack(navHandler, cards.cards)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ViewNewCardStack(navHandler: NavigationHandler, playingCards: List<PlayingCard>) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = {
            Text("View New Card Stack")
        }
    ) {
        CardStackPager(playingCards)
    }
}

@Preview
@Composable
fun ViewNewCardStackScreenPreview() {

    AppTheme {
        val list = listOf(
            samplePlayingCard,
            samplePlayingCard
        )
        ViewNewCardStack({ _, _ -> }, list)
    }
}
