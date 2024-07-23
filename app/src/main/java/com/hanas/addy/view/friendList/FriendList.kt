package com.hanas.addy.view.friendList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object FriendList : NavScreen

fun NavGraphBuilder.friendListComposable(navHandler: NavigationHandler) {
    composable<FriendList> {
        val viewModel: FriendListViewModel = koinNavViewModel()
        FriendListScreen(navHandler, viewModel::onSendRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(navHandler: NavigationHandler, onSendRequest: (String) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { Text("Friends") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openBottomSheet = true
                },
                shape = BlobShape(30f),
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) {

    }
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = sheetState,
            properties = ModalBottomSheetDefaults.properties(shouldDismissOnBackPress = true),
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            var email by rememberSaveable { mutableStateOf("") }
            Column(Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                Text("Add new user with email:")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { onSendRequest(email) })
                )
                Button(onClick = { onSendRequest(email) }) { Text("send request") }
            }
        }
    }

}

@Preview
@Composable
fun FriendListScreenPreview() {
    AppTheme {
        FriendListScreen({}) { }
    }
}