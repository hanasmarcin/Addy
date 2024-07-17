package com.hanas.addy.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    fun logout() {
        firebaseAuth.signOut()
    }

    init {
        firebaseAuth.addIdTokenListener { auth: FirebaseAuth ->
            userFlow.value = auth.currentUser
        }
    }

    val userFlow = MutableStateFlow<FirebaseUser?>(null)
}