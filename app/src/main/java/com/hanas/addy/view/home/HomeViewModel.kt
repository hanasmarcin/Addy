package com.hanas.addy.view.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
) : ViewModel() {
    fun logout() {
        Firebase.auth.signOut()
    }

    init {
        Firebase.auth.addAuthStateListener { auth: FirebaseAuth ->
            userFlow.value = auth.currentUser
        }
    }

    val userFlow = MutableStateFlow<FirebaseUser?>(null)
}