@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy.view.cardStackList

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.view.createNewCardStack.CreateNewCardStack
import com.hanas.addy.ui.GoBack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.view.home.NavigationHandler
import com.hanas.addy.model.PlayingCardStack
import com.hanas.addy.ui.samplePlayingCard
import com.hanas.addy.view.cardStackDetail.CardStackDetail
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.components.AppButton
import com.hanas.addy.ui.components.AppListItem
import com.hanas.addy.ui.components.itemsWithPosition
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.drawPattern
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
    cardStacks: List<PlayingCardStack>,
    navHandler: NavigationHandler,
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .drawPattern(R.drawable.graph_paper, tint = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)),
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.card_stack_list_screen_title)) }, navigationIcon = {
                FilledIconButton(
                    shape = BlobShape(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    onClick = { navHandler.navigate(GoBack, true) },
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            }, colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navHandler.navigate(CreateNewCardStack, true) },
                shape = BlobShape(30f),
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Add, null)
            }
        }) {
        AnimatedContent(
            targetState = cardStacks.isEmpty(),
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            label = ""
        ) { isEmpty ->
            if (isEmpty) {
                NoCardStacksContent(navHandler)
            } else {
                CardStacksListContent(navHandler, cardStacks)
            }
        }
    }
}

@Composable
private fun CardStacksListContent(navHandler: NavigationHandler, cardStacks: List<PlayingCardStack>) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsWithPosition(cardStacks) { cardStack, position, index ->
            AppListItem(
                position = position,
                trailingContent = {
                    CardStackSizePill(cardStack.cards.size)
                },
                pattern = if (index % 2 == 0) R.drawable.hideout else R.drawable.charlie_brown,
                onClick = {
                    cardStack.id?.let {
                        navHandler.navigate(CardStackDetail(it), false)
                    }
                }
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
                painter = painterResource(R.drawable.stacks_of_cards), contentDescription = null
            )
            Text(stringResource(R.string.card_stack_list_screen_empty_state_description))
            AppButton(
                onClick = { navHandler.navigate(CreateNewCardStack, true) },
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(stringResource(R.string.card_stack_list_screen_empty_state_button_label))
            }
        }
    }
}

class SamplePlayingCardStackListProvider : PreviewParameterProvider<List<PlayingCardStack>> {
    override val values = sequenceOf(
        emptyList(), listOf(
            PlayingCardStack(
                "ABC", listOf(samplePlayingCard, samplePlayingCard, samplePlayingCard),
            ),
            PlayingCardStack(
                "DEF", listOf(samplePlayingCard, samplePlayingCard),
            )
        )
    )
}

@Preview
@Composable
fun CardStackListScreenPreview(@PreviewParameter(SamplePlayingCardStackListProvider::class) cardStacks: List<PlayingCardStack>) {
    AppTheme {
        CardStackListScreen(cardStacks) { _, _ -> }
    }
}