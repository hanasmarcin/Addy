package com.hanas.addy.view.cardStackDetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.model.samplePlayCardStack
import com.hanas.addy.ui.CardStackPager
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
data class CardStackDetail(val id: String) : NavScreen

fun NavGraphBuilder.cardStackDetailComposable(navigateBack: () -> Unit) {
    composable<CardStackDetail> {
        val viewModel: CardStackDetailViewModel = koinNavViewModel()
        val cardStack by viewModel.cardStack.collectAsState()
        CardStackDetailScreen(
            cardStack = cardStack,
            navigateBack = navigateBack
        )
    }
}

@Composable
fun CardStackDetailScreen(
    cardStack: PlayCardStack?,
    navigateBack: () -> Unit,
) {
    AppScaffold(
        navigateBack = navigateBack,
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
        CardStackDetailScreen(samplePlayCardStack) { }
    }
}