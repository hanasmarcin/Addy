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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.hanas.addy.model.DataHolder
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.model.samplePlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppListItem
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.itemsWithPosition
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object CardStackList : NavScreen


fun NavGraphBuilder.cardStackListComposable(
    openCreateNewCardStack: () -> Unit,
    openCardStackDetail: (String) -> Unit,
    navigateBack: () -> Unit
) {
    composable<CardStackList> {
        val viewModel: CardStackListViewModel = koinNavViewModel()
        val cardStacks by viewModel.cardStacksFlow.collectAsState()
        CardStackListScreen(
            cardStacks = cardStacks,
            openCreateNewCardStack = openCreateNewCardStack,
            openCardStackDetail = openCardStackDetail,
            navigateBack = navigateBack,
        )
    }
}

@Composable
private fun CardStackListScreen(
    cardStacks: DataHolder<List<PlayCardStack>>,
    openCreateNewCardStack: () -> Unit,
    openCardStackDetail: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    AppScaffold(
        topBarTitle = { Text(stringResource(R.string.card_stack_list_screen_title)) },
        navigateBack = navigateBack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = openCreateNewCardStack,
                shape = BlobShape(30f),
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) {
        AnimatedContent(
            targetState = cardStacks.data?.isEmpty() == true,
            modifier = Modifier
                .fillMaxSize(),
            label = ""
        ) { isEmpty ->
            if (isEmpty) {
                NoCardStacksContent(openCreateNewCardStack)
            } else {
                CardStacksListContent(cardStacks.data.orEmpty(), false, false) { id ->
                    openCardStackDetail(id)
                }
            }
        }
    }
}

@Composable
fun CardStacksListContent(
    cardStacks: List<PlayCardStack>,
    areCardStacksLoading: Boolean,
    isGameCreationInProgress: Boolean,
    onClick: (String) -> Unit
) {
    var selectedStackId by rememberSaveable { mutableStateOf<String?>(null) }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsWithPosition(cardStacks) { cardStack, position, _ ->
            AppListItem(
                position = position,
                trailingContent = {
                    LabelPill("${cardStack.cards.size} cards")
                },
                enabled = isGameCreationInProgress.not(),
                onClick = {
                    cardStack.id?.let {
                        selectedStackId = it
                        onClick(it)
                    }
                },
                isLoading = isGameCreationInProgress && selectedStackId == cardStack.id,
            ) {
                Text(cardStack.title, Modifier.padding(horizontal = 4.dp))
            }
        }
    }
}

@Composable
fun LabelPill(text: String) {
    Surface(shape = CircleShape, color = containerFor(blue)) {
        Text(
            text,
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun NoCardStacksContent(openCreateNewCardStack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.Companion
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.stack_of_cards), contentDescription = null
            )
            Text(stringResource(R.string.card_stack_list_screen_empty_state_description))
            PrimaryButton(
                onClick = openCreateNewCardStack,
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
        CardStackListScreen(DataHolder.Success(cardStacks), {}, {}) { }
    }
}