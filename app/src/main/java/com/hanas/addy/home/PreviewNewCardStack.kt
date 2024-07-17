package com.hanas.addy.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.AppTheme
import org.koin.androidx.compose.navigation.koinNavViewModel

fun NavGraphBuilder.previewNewCardStackComposable(navHandler: NavigationHandler, navController: NavController) {
    composable<CreateNewCardStack.PreviewNewStack> {
        val parent = navController.getBackStackEntry<CreateNewCardStack>()
        val viewModel: CreateNewCardStackViewModel = koinNavViewModel(viewModelStoreOwner = parent)
        val cards by viewModel.outputContentFlow.collectAsState()
        PreviewNewCardStackScreen(cards)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun PreviewNewCardStackScreen(playingCards: List<PlayingCard>) {
    Scaffold {
        val pagerState = rememberPagerState { playingCards.size }
        HorizontalPager(pagerState, contentPadding = PaddingValues(horizontal = 32.dp), pageSpacing = 16.dp) { page ->
            val card = playingCards[page]
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Card {
                    Text(card.title)
                    Text(card.description)
                    Text(card.question)
                    Text(card.title)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewNewCardStackScreenPreview() {

    AppTheme {
        val list = listOf(
            samplePlayingCard,
            samplePlayingCard
        )
        PreviewNewCardStackScreen(list)
    }
}

val samplePlayingCard = PlayingCard(
    question = "Which of these factors is NOT a significant reason why the monsoon climate is ideal for rice production in Southeast Asia?",
    A = "Heavy rainfall during the monsoon season.",
    B = "Warm temperatures throughout the year.",
    C = "A long growing season with sufficient sunlight.",
    D = "Arid conditions that allow for efficient water management.",
    answer = "D",
    title = "The Monsoon Whisperer",
    description = "This wise character knows the secrets of the monsoon winds and their influence on Southeast Asia's agricultural bounty.",
    attributes = Attributes(
        green = Attribute(
            name = "Climate Knowledge",
            value = 5
        ),
        blue = Attribute(
            name = "Rice production",
            value = 8
        ),
        red = Attribute(
            name = "Monsoon expertise",
            value = 3
        )
    )
)