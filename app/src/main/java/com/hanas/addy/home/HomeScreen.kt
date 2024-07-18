package com.hanas.addy.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.hanas.addy.R
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.components.AppScaffold
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
    fun navigate(action: NavAction, closeCurrent: Boolean)
}

@Composable
fun HomeScreen(
    navHandler: NavigationHandler,
    username: String?,
    photoUrl: Uri?,
    logout: () -> Unit
) {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        navHandler = navHandler,
        hasBackButton = false,
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                AsyncImage(photoUrl, null)
            }
            DropdownMenu(menuExpanded, onDismissRequest = { menuExpanded = !menuExpanded }) {
                DropdownMenuItem({ Text("Logout") }, {
                    logout()
                    navHandler.navigate(Home, true)
                })
            }
        },
        topBarTitle = {
            Text("Hello, $username!")
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
            HomeMenuButton(
                title = "Play",
                color = MaterialTheme.colorScheme.primaryContainer
            ) {}
            HomeMenuButton(
                title = "Your card stacks",
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                navHandler.navigate(CardStackList, true)
            }
        }
    }
}

@Composable
fun HomeMenuButton(
    title: String,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(color),
        onClick = onClick
    ) {
        Row {
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen({ _, _ -> }, "Marcin", null, {})
    }
}