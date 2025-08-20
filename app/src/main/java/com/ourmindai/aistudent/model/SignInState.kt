package com.ourmindai.aistudent.model

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
