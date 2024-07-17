package com.hanas.addy.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    val user = firebaseAuth.currentUser
}