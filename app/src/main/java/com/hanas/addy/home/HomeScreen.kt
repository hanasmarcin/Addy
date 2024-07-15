@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.hanas.addy.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.ui.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

interface NavScreen

@Serializable
object Home : NavScreen

fun NavGraphBuilder.homeComposable(navigate: Navigate) {
    composable<Home> {
        val viewModel: HomeViewModel = koinNavViewModel()
        HomeScreen(navigate)
    }
}

typealias Navigate = (NavScreen) -> Unit

@Composable
fun HomeScreen(navigate: Navigate) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
                .drawPattern(R.drawable.graph_paper, tint = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f))
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center).padding(32.dp),
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
                    navigate(CardStackList)
                }
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
        HomeScreen {}
    }
}