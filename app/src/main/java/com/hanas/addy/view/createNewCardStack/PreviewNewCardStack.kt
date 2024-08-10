package com.hanas.addy.view.createNewCardStack

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.theme.AppTheme
import org.koin.androidx.compose.navigation.koinNavViewModel

fun NavGraphBuilder.viewNewCardStackComposable(navController: NavController, navigateBack: () -> Unit) {
    composable<CreateNewCardStack.PreviewNewStack> {
        val parent = remember(it) { navController.getBackStackEntry<CreateNewCardStack>() }
        val viewModel: CreateNewCardStackViewModel = koinNavViewModel(viewModelStoreOwner = parent)
        val cards by viewModel.cardStackFlow.collectAsState()
        ViewNewCardStack(cards.data, viewModel::deleteGeneratedStack, navigateBack)
    }
}

@Composable
private fun ViewNewCardStack(
    stack: Unit?,
    deleteGeneratedStack: () -> Unit,
    navigateBack: () -> Unit
) {
    BackHandler {
        deleteGeneratedStack()
        navigateBack()
    }
    AppScaffold(
        navigateBack = navigateBack,
        topBarTitle = {
//            Text(stack?.title.orEmpty())
        }
    ) {
//        CardStackPager(stack?.cards.orEmpty())
    }
}

@Preview
@Composable
fun ViewNewCardStackScreenPreview() {
    AppTheme {
//        ViewNewCardStack({ }, samplePlayCardStack, {})
    }
}
