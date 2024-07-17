package com.hanas.addy.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
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
        val user = viewModel.user
        HomeScreen(navigate, user?.displayName, user?.photoUrl)
    }
}

fun interface NavigationHandler {
    fun navigate(action: NavAction, closeCurrent: Boolean)
}

@Composable
fun HomeScreen(navHandler: NavigationHandler, username: String?, photoUrl: Uri?) {
    AppScaffold(
        navHandler = navHandler,
        hasBackButton = false,
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                AsyncImage(photoUrl, null)
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
        HomeScreen({ _, _ -> }, "Marcin", photoUrl = "https://picsum.photos/200/200".toUri())
    }
}