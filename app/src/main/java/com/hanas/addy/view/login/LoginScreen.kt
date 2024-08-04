package com.hanas.addy.view.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialResponse
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.home.Home
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel
import kotlin.coroutines.CoroutineContext

@Serializable
object Login : NavScreen

fun NavGraphBuilder.loginComposable(navigate: NavigationHandler) {
    composable<Login> {
        val viewModel: LoginViewModel = koinNavViewModel()
        val context = LocalContext.current
        val loginState by viewModel.loginStateFlow.collectAsState()
        LoginScreen(navigate, loginState) {
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
    navHandler: NavigationHandler,
    loginState: LoginState,
    login: suspend () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val errorHandler = object : CoroutineExceptionHandler {
        override val key = CoroutineExceptionHandler
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            Log.d("HANASSS", exception.stackTraceToString())
        }
    }
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navHandler.navigate(Home)
            }
            is LoginState.Error -> {
                Toast.makeText(context, loginState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                /* Do nothing */
            }
        }
    }
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = null,
        hasBackButton = false
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        )
        {
            Box(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        AppColors
                            .containerFor(AppColors.pink)
                            .compositeOver(MaterialTheme.colorScheme.background)
                    )
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    onClick = {
                        coroutineScope.launch(errorHandler) {
                            login()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
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
        LoginScreen({ }, LoginState.NotLoggedIn, {})
    }
}

const val TAG = "HANASSS"
