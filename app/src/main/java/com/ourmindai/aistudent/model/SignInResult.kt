package com.ourmindai.aistudent.model

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
)

// when we login with google
data class  UserData (
    val userId: String,
    val userName: String?,
    val userEmail: String?,
    val profilePictureUrl: String?
)

sealed class AuthState {
    object Authenticated: AuthState()  // if we are in authenticate then we are in Home page
    object Unauthenticate: AuthState() // if we are in Unauthenticate then we are in Login page
    object Loading: AuthState()

    data class Error(val message: String): AuthState()  // if we have error then we show the error
}