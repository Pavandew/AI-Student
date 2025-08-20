package com.ourmindai.aistudent.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ourmindai.aistudent.R
import com.ourmindai.aistudent.model.AuthState
import com.ourmindai.aistudent.ui.theme.Typography
import com.ourmindai.aistudent.viewmodel.AuthViewModel


@Composable
fun SignUpPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    var userName by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var rePassword by remember {
        mutableStateOf("")
    }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context,
                (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        // background Image for login
        Image(
            painter = painterResource(id = R.drawable.bg_login_img),
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
                contentDescription = null, modifier = Modifier.size(150.dp).padding(top = 30.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Sign Up",
                style = Typography.headlineLarge,
                color = Color.Black,
                fontSize = 32.sp,
                modifier = modifier.padding(top = 54.dp)
            )

//            Spacer(modifier = modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(60.dp),
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter Your Email") },
                singleLine = true //
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(60.dp),
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Enter Your UserName") },
                singleLine = true //
            )

            Spacer(modifier = Modifier.height(8.dp)) // Smaller space between fields

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(60.dp),
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Your Password") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(60.dp),
                value = rePassword,
                textStyle = Typography.bodyLarge.copy(color = Color.Black),
                onValueChange = { rePassword = it },
                label = { Text("Re-enter Password") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                shape = CutCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3), // Blue background
                    contentColor = Color.White         // White text
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(150.dp),
                onClick = {
                    Log.d("SignUpPage", "userName: ${userName} email: ${email} , password: ${password}, re-password: ${rePassword}")
                    authViewModel.signup(userName, email, password, rePassword)
                },
            ) {
                Text("Sign Up")
            }

            TextButton (
                onClick = {
                    navController.navigate("login")
                },
                enabled = authState != AuthState.Loading
            ) {
                Text(" have an account, login",
                    color = Color.Black)
            }
        }
    }
}