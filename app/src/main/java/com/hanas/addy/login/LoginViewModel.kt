package com.hanas.addy.login

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hanas.addy.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginState {
    data object NotLoggedIn : LoginState()
    data object LoggingIn : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(val firebaseAuth: FirebaseAuth) : ViewModel() {
    val loginStateFlow: StateFlow<LoginState>
        get() = _loginStateFlow
    private val _loginStateFlow = MutableStateFlow<LoginState>(LoginState.NotLoggedIn)

    fun handleSignIn(result: GetCredentialResponse) {
        _loginStateFlow.value = LoginState.LoggingIn
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        firebaseAuth.signInWithCredential(firebaseCredential)
                        _loginStateFlow.value = LoginState.Success
                    } catch (e: GoogleIdTokenParsingException) {
                        _loginStateFlow.value = LoginState.Error(e.localizedMessage ?: "")
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    _loginStateFlow.value = LoginState.Error("Unexpected type of credential")
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                _loginStateFlow.value = LoginState.Error("Unexpected type of credential")
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
}

fun getCredentialRequest(): GetCredentialRequest {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.googleIdserverClientId)
        .setAutoSelectEnabled(true)
        .build() //TODO: Add nonce


    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    return request
}