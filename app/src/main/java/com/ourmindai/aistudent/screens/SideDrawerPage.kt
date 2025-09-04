package com.ourmindai.aistudent.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ourmindai.aistudent.R
import com.ourmindai.aistudent.model.UserData
import com.ourmindai.aistudent.ui.theme.Typography
import com.ourmindai.aistudent.viewmodel.AuthViewModel
import com.ourmindai.aistudent.viewmodel.GeminiViewModel
import kotlinx.coroutines.launch

@Composable
fun SideDrawerPage(
    drawerState: DrawerState,
    onOption1Click: () -> Unit,
    onOption2Click: () -> Unit,
    userData: UserData,
    authViewModel: AuthViewModel,
    geminiViewModel: GeminiViewModel
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        geminiViewModel.fetchChatHistory()
    }
//
//    val drawerItems = listOf(
//        SideDrawerItem("New Chat", {
//
//            scope.launch{
//                drawerState.close()  // close drawer
//                geminiViewModel.clearChat()
//                Log.d("SideDrawerPage", "New Chat Page")
//                onOption1Click()
//            }
//        })
//    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(end = 90.dp)
        .background(Color.Black)
    ) {
//        Text(
//            "Navigation Menu",
//            modifier = Modifier.padding(top = 40.dp, start = 16.dp),
//            color = Color.White,
//            style = Typography.headlineMedium)
//
//        HorizontalDivider(modifier = Modifier.padding(8.dp))

        TextButton(
            onClick = {
                scope.launch {
                    drawerState.close()
                    geminiViewModel.clearChat()
                    onOption1Click
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 45.dp)
        ) {
            Text(
                text = "New Chat",
                style = Typography.headlineSmall.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        )

        LazyColumn (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){

            // Header for chat history
            item {
                Text(
                    text = "Your Chat History",
                    style = Typography.bodyLarge,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 6.dp)
                )
            }

            // Show chat history from Firestore
            items(geminiViewModel.chatHistory.sortedByDescending { it.timestamp }) { chat ->
                TextButton(
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            geminiViewModel.loadChatById(chat.chatId)
                            onOption1Click()
                        }
                    }
                ) {
                    Text(
                        text = "Chat: ${geminiViewModel.formatTimestamp(chat.timestamp)}",
                        style = Typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

//        Spacer(modifier = Modifier.weight(0.1f)) // Push everything below to bottom

        // Text Button for signOut
        TextButton(
            onClick = {
                authViewModel.signOut()
            },
            modifier = Modifier
                .fillMaxWidth()
//                .padding(16.dp)
        ) {
            Text(text = "Sign Out", style = Typography.bodyLarge.copy(color = Color.White))
        }

//        // row for Showing user Image and userName
//        // Bottom user info
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp, bottom = 40.dp)
//                .verticalScroll(rememberScrollState()),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
////            if (userData.profilePictureUrl != null) {
//                AsyncImage(
//                    model = userData.profilePictureUrl  ?: R.drawable.chat_icon,
//                    contentDescription = "Profile Picture",
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop
//                )
////            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column {
//                Text(
//                    text = userData.userName ?: "Guest",
//                    color = Color.White,
//                    style = Typography.bodyLarge
//                )
//                Text(
//                    text =  userData.userEmail ?: "No Email",
//                    color = Color.LightGray,
//                    style = Typography.bodySmall
//                )
//            }
//        }
    }
}