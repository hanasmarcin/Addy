package com.hanas.addy.view.cardStackDetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.PlayingCardStack
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.CardStackPager
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.samplePlayingCard
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
data class CardStackDetail(val id: String) : NavScreen

fun NavGraphBuilder.cardStackDetailComposable() {
    composable<CardStackDetail> {
        val viewModel: CardStackDetailViewModel = koinNavViewModel()
        val cardStack by viewModel.cardStack.collectAsState()
        CardStackDetailScreen(
            cardStack = cardStack,
            navHandler = {  }
        )
    }
}

@Composable
fun CardStackDetailScreen(
    cardStack: PlayingCardStack?,
    navHandler: NavigationHandler
) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { cardStack?.let { Text(cardStack.title) } }
    ) {
        cardStack?.cards?.let {
            CardStackPager(it)
        }
    }
}

@Preview
@Composable
fun CardStackDetailScreenPreview() {
    AppTheme {
        CardStackDetailScreen(PlayingCardStack("ABC", listOf(samplePlayingCard, samplePlayingCard))) {  }
    }
}