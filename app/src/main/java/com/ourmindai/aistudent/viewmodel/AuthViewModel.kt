package com.ourmindai.aistudent.viewmodel

import androidx.lifecycle.ViewModel
import com.ourmindai.aistudent.firebaseauth.LoginAuth
import com.ourmindai.aistudent.firebaseauth.SignOut
import com.ourmindai.aistudent.firebaseauth.SignUpAuth
import com.ourmindai.aistudent.model.AuthState
import com.ourmindai.aistudent.model.SignInResult
import com.ourmindai.aistudent.model.SignInState
import com.ourmindai.aistudent.model.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel(){

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData = _userData.asStateFlow()

    // Sign in with google
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticate)
    val authState = _authState.asStateFlow()

    // checking Auth
    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked = _isAuthChecked.asStateFlow()


    private val loginAuth = LoginAuth(auth, _authState) {
        checkAuthStatus()
    }

    private val signUpAuth = SignUpAuth(auth, _authState)

    private val signOut = SignOut(auth, _authState)

    fun resetState() {
        _state.update { SignInState() }
    }

    init {
        checkAuthStatus()
    }

    // Google Sign-In Result Handler
    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(

            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage

        ) }
        if (result.data != null) {
            _userData.value = result.data  // set User data

            checkAuthStatus()
        }
    }

    // This method will check we are logged in or not
    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if(currentUser == null) {
            _authState.value = AuthState.Unauthenticate
            _userData.value = null
        } else {
            _authState.value = AuthState.Authenticated
            _userData.value = UserData(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: currentUser.email?.substringBefore("@")?: "Guest",
                userEmail = currentUser.email,
                profilePictureUrl = currentUser.photoUrl?.toString()
            )
        }

        _isAuthChecked.value = true
    }

    // Authentication for Login
    fun login(email: String, password: String) = loginAuth.login(email, password)

    // Authentication for Sign In
    fun signup(userName: String, email: String, password: String, repassword: String) = signUpAuth.signup(userName, email, password, repassword)

    // method for signOut
    fun signOut(chatViewModel: GeminiViewModel? = null) {
        signOut.signOut()
    }

}