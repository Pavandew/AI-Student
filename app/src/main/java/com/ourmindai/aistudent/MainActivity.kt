package com.ourmindai.aistudent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ourmindai.aistudent.model.AuthState
import com.ourmindai.aistudent.model.UserData
import com.ourmindai.aistudent.navigation.ScreenNavigation
import com.ourmindai.aistudent.screens.SplashScreen
import com.ourmindai.aistudent.ui.theme.AIStudentTheme
import com.ourmindai.aistudent.viewmodel.AuthViewModel
import com.ourmindai.aistudent.viewmodel.GeminiViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    // Initialize ViewModels
    private val authViewModel: AuthViewModel by viewModels()
    private val geminiViewModel: GeminiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userData by authViewModel.userData.collectAsState()
            val authState by authViewModel.authState.collectAsState()
            val isAuthChecked by authViewModel.isAuthChecked.collectAsState()
            var isSplashDone by remember { mutableStateOf(false) }

            val startDestination = if (authState is AuthState.Authenticated) "home" else "login"

            // Splash delay simulation
            LaunchedEffect(Unit) {
                delay(2000)
                isSplashDone = true
            }

            AIStudentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(!isSplashDone) {
                        SplashScreen()
                    } else if(isAuthChecked){
                        ScreenNavigation(modifier =
                            Modifier.padding(innerPadding),
                            geminiViewModel,
                            authViewModel,
                            userData = userData ?: UserData("", "Guest", "Email", ""),
                            startDestination = startDestination
                            )
                    }
                }
            }
        }
    }
}
