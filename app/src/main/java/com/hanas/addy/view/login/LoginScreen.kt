package com.hanas.addy.view.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialResponse
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.gameSession.chooseGameSession.observeNavigation
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel
import kotlin.coroutines.CoroutineContext

@Serializable
object Login : NavScreen

fun NavGraphBuilder.loginComposable(navController: NavController) {
    composable<Login> {
        val viewModel: LoginViewModel = koinNavViewModel()
        val context = LocalContext.current
        val loginState by viewModel.loginStateFlow.collectAsState()
        viewModel.observeNavigation<Login>(navController)
        LoginScreen(loginState) {
            login(
                context = context,
                credentialManager = CredentialManager.create(context),
                handleSignIn = viewModel::handleSignIn,
            )
        }
    }
}

private suspend fun login(
    credentialManager: CredentialManager,
    handleSignIn: (GetCredentialResponse) -> Unit,
    context: Context
) {
    val result = credentialManager.getCredential(
        request = getCredentialRequest(),
        context = context, //activity context needed
    )
    handleSignIn(result)
}

@Composable
fun LoginScreen(
    loginState: LoginState,
    login: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val errorHandler = object : CoroutineExceptionHandler {
        override val key = CoroutineExceptionHandler
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            exception.printStackTrace()
        }
    }
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Error -> {
                Toast.makeText(context, loginState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                /* Do nothing */
            }
        }
    }
    AppScaffold(
        topBarTitle = { },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Image(
                painterResource(R.drawable.kid_login),
                null,
                Modifier.weight(1f)
            )
            Box(
                Modifier
                    .padding(bottom = 32.dp)
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerFor(AppColors.pink))
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    onClick = {
                        coroutineScope.launch(errorHandler) {
                            login()
                        }
                    },
                    modifier = Modifier.widthIn(min = 160.dp),
                    isLoading = loginState is LoginState.LoggingIn
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(LoginState.NotLoggedIn, {})
    }
}

const val TAG = "ADDY"
