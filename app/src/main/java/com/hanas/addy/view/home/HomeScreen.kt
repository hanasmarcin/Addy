package com.hanas.addy.view.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.hanas.addy.R
import com.hanas.addy.ui.NavAction
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppButton
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.friendList.FriendList
import com.hanas.addy.view.cardStackList.CardStackList
import com.hanas.addy.view.playTable.PlayTable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object Home : NavScreen

fun NavGraphBuilder.homeComposable(navigate: NavigationHandler) {
    composable<Home> {
        val viewModel: HomeViewModel = koinNavViewModel()
        val user by viewModel.userFlow.collectAsState()
        HomeScreen(
            navHandler = navigate,
            username = user?.displayName,
            photoUrl = user?.photoUrl,
            logout = viewModel::logout
        )
    }
}

fun interface NavigationHandler {
    fun navigate(action: NavAction)
}

@Composable
fun HomeScreen(
    navHandler: NavigationHandler,
    username: String?,
    photoUrl: Uri?,
    logout: () -> Unit
) {
    AppScaffold(
        navHandler = navHandler,
        modifier = Modifier.fillMaxSize(),
        hasBackButton = false,
        topBarTitle = {
            Text("Hello, $username!")
        },
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                AsyncImage(photoUrl, null)
            }
            DropdownMenu(menuExpanded, onDismissRequest = { menuExpanded = !menuExpanded }) {
                DropdownMenuItem({ Text("Logout") }, {
                    logout()
                    navHandler.navigate(Home)
                })
            }
        }
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            painter = painterResource(R.drawable.teens_playing),
            contentDescription = null
        )
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppButton(
                onClick = { navHandler.navigate(PlayTable) },
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("Play")
            }
            AppButton(
                onClick = { navHandler.navigate(CardStackList) },
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text("Your card stacks")
            }
            AppButton(
                onClick = { navHandler.navigate(FriendList) },
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text("Friends")
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen({ }, "Marcin", null, {})
    }
}