package com.ourmindai.aistudent.firebaseauth

import com.ourmindai.aistudent.model.AuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

class LoginAuth(
    private val auth: FirebaseAuth,
    private val authState: MutableStateFlow<AuthState>,
    private val onLoginSuccess: () -> Unit
) {
    fun login(email: String, password: String) {
        if(email.isBlank() || password.isBlank()) {
            authState.value = AuthState.Error("Email and Password can't be empty")
            return
        }

        authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    authState.value = AuthState.Authenticated
                    onLoginSuccess()
                } else {
//                    AuthState.Error(task.exception?.message ?: "Something went wrong")
                    val errorMessage = task.exception?.message ?: "Something went wrong"
                    authState.value = AuthState.Error(errorMessage)

                }
            }
    }
}