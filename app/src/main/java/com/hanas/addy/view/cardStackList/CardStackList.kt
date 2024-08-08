@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy.view.cardStackList

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppListItem
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.itemsWithPosition
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.cardStackDetail.CardStackDetail
import com.hanas.addy.view.createNewCardStack.CreateNewCardStack
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object CardStackList : NavScreen


fun NavGraphBuilder.cardStackListComposable(navHandler: NavigationHandler) {
    composable<CardStackList> {
        val viewModel: CardStackListViewModel = koinNavViewModel()
        val cardStacks by viewModel.cardStacksFlow.collectAsState()
        CardStackListScreen(
            cardStacks = cardStacks,
            navHandler = navHandler,
        )
    }
}

@Composable
private fun CardStackListScreen(
    cardStacks: List<PlayCardStack>,
    navHandler: NavigationHandler,
) {
    AppScaffold(
        topBarTitle = { Text(stringResource(R.string.card_stack_list_screen_title)) },
        navHandler = navHandler,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navHandler.navigate(CreateNewCardStack) },
                shape = BlobShape(30f),
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) {
        AnimatedContent(
            targetState = cardStacks.isEmpty(),
            modifier = Modifier
                .fillMaxSize(),
            label = ""
        ) { isEmpty ->
            if (isEmpty) {
                NoCardStacksContent(navHandler)
            } else {
                CardStacksListContent(cardStacks) { id ->
                    navHandler.navigate(CardStackDetail(id))
                }
            }
        }
    }
}

@Composable
fun CardStacksListContent(cardStacks: List<PlayCardStack>, onClick: (String) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsWithPosition(cardStacks) { cardStack, position, _ ->
            AppListItem(
                position = position,
                trailingContent = {
                    CardStackSizePill(cardStack.cards.size)
                },
                onClick = { cardStack.id?.let { onClick(it) } }
            ) {
                Text(cardStack.title, Modifier.padding(horizontal = 4.dp))
            }
        }
    }
}

@Composable
private fun CardStackSizePill(size: Int) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
        Text(
            "$size cards",
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun NoCardStacksContent(navHandler: NavigationHandler) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.Companion
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.stacks_of_cards_for_a_custom_educational_game___1_), contentDescription = null
            )
            Text(stringResource(R.string.card_stack_list_screen_empty_state_description))
            PrimaryButton(
                onClick = { navHandler.navigate(CreateNewCardStack) },
            ) {
                Text(stringResource(R.string.card_stack_list_screen_empty_state_button_label))
            }
        }
    }
}

class SamplePlayCardStackListProvider : PreviewParameterProvider<List<PlayCardStack>> {
    override val values = sequenceOf(
        emptyList(), listOf(
            samplePlayCardStack,
//            PlayCardStack(
//                "DEF", listOf(samplePlayCard, samplePlayCard),
//            ),
//            samplePlayCardStack
        )
    )
}

@Preview
@Composable
fun CardStackListScreenPreview(@PreviewParameter(SamplePlayCardStackListProvider::class) cardStacks: List<PlayCardStack>) {
    AppTheme {
        CardStackListScreen(cardStacks) { }
    }
}