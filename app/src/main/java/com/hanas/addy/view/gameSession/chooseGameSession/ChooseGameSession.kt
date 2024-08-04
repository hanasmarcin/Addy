package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.DataHolder
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppColors.pink
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object ChooseSession : NavScreen

fun NavGraphBuilder.chooseGameSessionComposable(navHandler: NavigationHandler) {
    composable<ChooseSession> {
        val viewModel: ChooseGameSessionViewModel = koinNavViewModel()
        val state by viewModel.state.collectAsState()
        viewModel.observeNavigation(navHandler)
        ChooseGameSessionScreen(navHandler, state, viewModel::joinSession)
    }
}

@Composable
fun ChooseGameSessionScreen(
    navHandler: NavigationHandler,
    state: DataHolder<String>,
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
                PrimaryButton(
                    onClick = { navHandler.navigate(ChooseCardStack) },
                    color = blue,
                    isLoading = state is DataHolder.Loading
                ) {
                    Text("Create new game")
                }
            }

        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerFor(pink))) {
                Column(Modifier.padding(16.dp)) {
                    Text("Join with code", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.size(8.dp))
                    var code by rememberSaveable { mutableStateOf("") }
                    AppInput(
                        enabled = state !is DataHolder.Loading,
                        isError = state is DataHolder.Error,
                        supportingText = if (state is DataHolder.Error) (@Composable { Text(state.error.message.orEmpty()) }) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        value = code,
                        onValueChange = { code = it.filter { it.isDigit() }.take(6) },
                        label = { Text("Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(Modifier.size(8.dp))
                    PrimaryButton({ joinGameSession(code) }, Modifier.fillMaxWidth(), isLoading = state is DataHolder.Loading) { Text("Join") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInput(
    enabled: Boolean,
    isError: Boolean,
    supportingText: @Composable (() -> Unit)?,
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)?,
    keyboardOptions: KeyboardOptions,
    singleLine: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        modifier = modifier.padding(top = 5.dp),
        value = value,
        singleLine = true,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Color.White),
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = innerTextField,
            enabled = enabled,
            singleLine = singleLine,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            label = label,
            supportingText = supportingText,
            container = {
                val colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = pink,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
                val shape = RoundedCornerShape(8.dp)
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(pink)
                        .padding(4.dp, 2.dp, 4.dp, 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        )
    }
}

@Composable
@Preview
fun ChooseGameSessionScreenPreview() {
    AppTheme {
        ChooseGameSessionScreen({}, DataHolder.Idle(), {})
    }
}