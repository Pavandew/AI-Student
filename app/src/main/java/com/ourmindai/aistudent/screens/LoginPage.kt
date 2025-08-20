package com.ourmindai.aistudent.screens

import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ourmindai.aistudent.firebaseauth.GoogleAuthUIClient
import com.ourmindai.aistudent.R
import com.ourmindai.aistudent.model.AuthState
import com.ourmindai.aistudent.ui.theme.Typography
import com.ourmindai.aistudent.viewmodel.AuthViewModel
import com.ourmindai.aistudent.viewmodel.GeminiViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    chatViewModel: GeminiViewModel
    ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    val googleAuthUIClient by remember {
        mutableStateOf(
            GoogleAuthUIClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        )
    }

    val authState by authViewModel.authState.collectAsState()
    val googleState by authViewModel.state.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                scope.launch {
                    val signInResult = googleAuthUIClient.signInWithIntent(intent)
                    authViewModel.onSignInResult(signInResult)
                }
            }
        }
    }

        // handle successful login
    LaunchedEffect(key1 = googleState.isSignInSuccessful) {
        if (googleState.isSignInSuccessful) {
            Log.d("LoginPage", "Login to Home page")
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    // Handle auth error message
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    // React to Success states
    LaunchedEffect(authState, googleState.isSignInSuccessful) {
//        if (googleState.isSignInSuccessful) {
//            navController.navigate("home") {
//                popUpTo("login") { inclusive = true }
//            }
//            authViewModel.resetState()
//        }
//
//        if (authState is AuthState.Error) {
//            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
//        }

//        if (googleState.signInError != null) {
//            Toast.makeText(context, googleState.signInError, Toast.LENGTH_SHORT).show()
//        }
//            if(googleState.isSignInSuccessful || authState is AuthState.Authenticated) {
//                navController.navigate("home") {
//                    popUpTo("login") { inclusive = true}
//                }
//            }
//
        if (authState is AuthState.Error) {
            val msg = (authState as AuthState.Error).message
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
        when(authState) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState as AuthState.Error).message,
                Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // background Image for login
        Image(
            painter = painterResource(id = R.drawable.bg_login_img), // Replace with your image
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = R.drawable.chat_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 30.dp),
//                    .sizeIn(minWidth = 70.dp, minHeight = 70.dp)
//                    .aspectRatio(2f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Login",
                style = Typography.headlineLarge,
                color = Color.Black,
                fontSize = 32.sp,
                modifier = modifier.padding(top = 54.dp)
            )
            // Email Field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .heightIn(min = 56.dp),
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true, //
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .heightIn(min = 56.dp),
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Your Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.login(email, password)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3), // background
                    contentColor = Color.White  // text color
                ),
                enabled = authState != AuthState.Loading,
                shape = CutCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(200.dp),
            ) {
                Text("Login", style = Typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Login Button
            Button(
                onClick = {
                    scope.launch {
                        val intentSender = googleAuthUIClient.signIn()
                        intentSender?.let {
                            launcher.launch(IntentSenderRequest.Builder(it).build())
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3), // background
                    contentColor = Color.White         //  text
                ),
                enabled = authState != AuthState.Loading,
                shape = CutCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp),
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.9f)
//                    .width(300.dp),
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(R.drawable.android_lightx),
                        contentDescription = "singInWithGoogle",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = modifier.padding(start = 8.dp))
                    Text("Login With Google", style = Typography.bodyLarge)
                }
            }

            TextButton (
                onClick = {
                    navController.navigate("signup")
                }
            ) {
                Text(
                    text = "Don't have an account, SignUp",
                    style = Typography.bodySmall,
                    color = Color.Black)
            }
        }
    }
}