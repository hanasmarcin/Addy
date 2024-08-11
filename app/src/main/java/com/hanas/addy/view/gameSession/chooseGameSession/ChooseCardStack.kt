package com.hanas.addy.view.gameSession.chooseGameSession

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.model.DataHolder
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.cardStackList.CardStacksListContent
import com.hanas.addy.view.cardStackList.SamplePlayCardStackListProvider
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object ChooseCardStack : NavScreen

fun NavGraphBuilder.chooseCardStackComposable(navController: NavController, navigateBack: () -> Unit) {
    composable<ChooseCardStack> {
        val viewModel: ChooseCardStackViewModel = koinNavViewModel()
        val state by viewModel.stateFlow.collectAsState()
        val (createGameState, cardStacksState) = state
        viewModel.observeNavigation<ChooseCardStack>(navController)
        ChooseCardStackScreen(cardStacksState, createGameState, viewModel::onChooseCardStack, navigateBack)
    }
}

@Composable
fun ChooseCardStackScreen(
    cardStacks: DataHolder<List<PlayCardStack>>,
    createGameState: DataHolder<String>,
    onChooseCardStack: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    AppScaffold(
        navigateBack = navigateBack,
        topBarTitle = { Text(stringResource(R.string.choose_card_stack_screen_title)) }
    ) {
        CardStacksListContent(
            cardStacks = cardStacks.data.orEmpty(),
            areCardStacksLoading = cardStacks is DataHolder.Loading,
            isGameCreationInProgress = createGameState is DataHolder.Loading,
        ) { id ->
            onChooseCardStack(id)
        }
    }
}

@Preview
@Composable
fun ChooseCardStackScreenPreview(@PreviewParameter(SamplePlayCardStackListProvider::class) cardStacks: List<PlayCardStack>) {
    AppTheme {
        ChooseCardStackScreen(DataHolder.Success(cardStacks), DataHolder.Idle(), {}) {}
    }
}

@SuppressLint("ComposableNaming")
@Composable
inline fun <reified T : Any> NavigationRequester.observeNavigation(navController: NavController) {
    val navigationRequest by navigationRequestFlow.collectAsState(initial = null)
    LaunchedEffect(navigationRequest) {
        navigationRequest?.let { request ->
            navController.navigate(request.targetScreen) {
                if (request.closeCurrentActivity) {
                    popUpTo<T> {
                        inclusive = true
                    }
                }
            }
        }
    }
}
