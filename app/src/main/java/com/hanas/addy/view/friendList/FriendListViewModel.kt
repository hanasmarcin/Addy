package com.hanas.addy.view.friendList

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendListViewModel : ViewModel() {
    fun onSendRequest(email: String) {

    }

}

class FriendListRepository {
    private val auth by lazy { Firebase.auth }
    private val database by lazy { Firebase.firestore }

    fun sendFriendRequest(email: String) {
        val user = auth.currentUser ?: throw Exception("User not authenticated")
//        database.collection(("friends").document(email).set({})
    }

}

data class AddFriendRequest(
    val email: String
)