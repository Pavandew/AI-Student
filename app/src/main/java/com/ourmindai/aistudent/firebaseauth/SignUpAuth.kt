package com.ourmindai.aistudent.firebaseauth

import android.util.Log
import com.ourmindai.aistudent.model.AuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

class SignUpAuth(
    private val auth: FirebaseAuth,
    private val _authState: MutableStateFlow<AuthState>

) {
    // Authentication for Sign In
    fun signup(userName: String, email: String, password: String, repassword: String) {
        if(email.isBlank() || password.isBlank()|| repassword.isBlank()) {
            _authState.value = AuthState.Error("Email or Password can't e empty")
            return
        }
        if (password != repassword) {
            // Show error: passwords do not match
            _authState.value = AuthState.Error("Password do not match")
            return
        }
        // before login we need to show loading
        _authState.value = AuthState.Loading

        // After loading trying to login
        auth.createUserWithEmailAndPassword(email, password) // this will take some time to complete
            .addOnCompleteListener { task ->   // if this completed then firebase provide some task
                if(task.isSuccessful) {
                    Log.d("AuthViewModel", "signUp successful: ${_authState.value}" )
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?: "Something went wrong")
                    Log.d("AuthViewModel", "signUp Unsuccessful: ${_authState.value}" )
                }
            }
    }
}