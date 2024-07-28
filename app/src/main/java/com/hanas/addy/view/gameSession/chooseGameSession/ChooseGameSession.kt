package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object ChooseSession : NavScreen

fun NavGraphBuilder.chooseGameSessionComposable(navHandler: NavigationHandler) {
    composable<ChooseSession> {
        val viewModel: ChooseGameSessionViewModel = koinNavViewModel()
        viewModel.observeNavigation(navHandler)
        ChooseGameSessionScreen(navHandler, viewModel::joinSession)
    }
}

@Composable
fun ChooseGameSessionScreen(
    navHandler: NavigationHandler,
    joinGameSession: (String) -> Unit
) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { Text("Choose Table") },
        bottomBar = {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                PrimaryButton(onClick = {
                    navHandler.navigate(ChooseCardStack)
                }) {
                    Text("Create new game")
                }
            }

        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Join with code", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.size(8.dp))
            Card {
                Column(Modifier.padding(16.dp)) {
                    var code by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = code,
                        onValueChange = { code = it.filter { it.isDigit() }.take(6) },
                        label = { Text("Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.size(8.dp))
                    PrimaryButton({ joinGameSession(code) }, Modifier.fillMaxWidth()) { Text("Join") }
                }
            }
        }
    }
}

@Composable
@Preview
fun ChooseGameSessionScreenPreview() {
    AppTheme {
        ChooseGameSessionScreen({}, {})
    }
}