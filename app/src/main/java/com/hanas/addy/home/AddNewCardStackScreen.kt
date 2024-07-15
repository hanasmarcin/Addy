
package com.hanas.addy.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object AddNewCardStack : NavScreen

fun NavGraphBuilder.addNewCardStackComposable() {
    composable<AddNewCardStack> {
        val viewModel: HomeViewModel = koinNavViewModel()
        AddNewCardStackScreen()
    }
}

@Composable
fun AddNewCardStackScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        )
        {
            Box(Modifier.padding(16.dp))
            {
                Card(Modifier.fillMaxSize()) {
                    Column(Modifier.padding(16.dp)) {
                        HomeMenuButton("Choose from gallery") { }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddNewCardStackScreenPreview() {
    AppTheme {
        AddNewCardStackScreen()
    }
}
