package com.hanas.addy.view.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.hanas.addy.R
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object Home : NavScreen

fun NavGraphBuilder.homeComposable(
    openPlayScreen: () -> Unit,
    openCardStackList: () -> Unit,
    navigateBackToLogin: () -> Unit
) {
    composable<Home> {
        val viewModel: HomeViewModel = koinNavViewModel()
        val user by viewModel.userFlow.collectAsState()
        HomeScreen(
            username = user?.displayName,
            photoUrl = user?.photoUrl,
            openPlayScreen = openPlayScreen,
            openCardStackList = openCardStackList,
            logout = {
                viewModel.logout()
                navigateBackToLogin()
            },
        )
    }
}

@Composable
fun HomeScreen(
    username: String?,
    photoUrl: Uri?,
    openPlayScreen: () -> Unit,
    openCardStackList: () -> Unit,
    logout: () -> Unit
) {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            AsyncImage(
                photoUrl,
                null,
                Modifier
                    .clip(BlobShape(60f))
                    .clickable(
                        onClick = { menuExpanded = !menuExpanded },
                        role = Role.Button
                    ),
            )
            DropdownMenu(menuExpanded, onDismissRequest = { menuExpanded = !menuExpanded }) {
                DropdownMenuItem({ Text("Logout") }, logout)
            }
        },
        topBarTitle = {
            Text("Hello, $username!")
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                painter = painterResource(R.drawable.kids_playing),
                contentDescription = null
            )
            Column(
                Modifier
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.containerFor(AppColors.orange))
                    .padding(16.dp)
                    .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppColors.orange,
                    onClick = openPlayScreen,
                ) {
                    Text("Play", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                PrimaryButton(
                    color = AppColors.orange,
                    onClick = openCardStackList,
                ) {
                    Text(
                        "Your card stacks",
                        Modifier
                            .padding(horizontal = 32.dp)
                            .requiredWidth(IntrinsicSize.Max)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen("Marcin", null, {}, {}, {})
    }
}