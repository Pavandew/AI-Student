package com.ourmindai.aistudent.firebaseauth

import com.ourmindai.aistudent.model.AuthState
import com.ourmindai.aistudent.viewmodel.GeminiViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow

class SignOut(
    private  val auth: FirebaseAuth,
    private val _authState: MutableStateFlow<AuthState>
) {
    // Method for SignOut
    fun signOut(chatViewModel: GeminiViewModel? = null) {
        try {
            auth.signOut()
//            chatViewModel?.clearChat()  // clear UI message if avaliable
            _authState.value = AuthState.Unauthenticate
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw  e
        }
    }
}