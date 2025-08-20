package com.ourmindai.aistudent.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ourmindai.aistudent.model.UserData
import com.ourmindai.aistudent.screens.HomePage
import com.ourmindai.aistudent.screens.LoginPage
import com.ourmindai.aistudent.screens.SignUpPage
import com.ourmindai.aistudent.viewmodel.AuthViewModel
import com.ourmindai.aistudent.viewmodel.GeminiViewModel

@Composable
fun ScreenNavigation(
    modifier: Modifier = Modifier,
    geminiViewModel: GeminiViewModel,
    authViewModel: AuthViewModel,
    userData: UserData,
    startDestination: String
    ) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = {
            composable("login") {
                LoginPage(modifier, navController, authViewModel, geminiViewModel)
            }
            composable("signup") {
                SignUpPage(modifier, navController, authViewModel)
            }
            composable("home") {
                HomePage(modifier, geminiViewModel, navController, userData, authViewModel)
            }
        }
    )


}