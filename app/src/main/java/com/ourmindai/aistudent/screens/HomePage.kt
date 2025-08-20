package com.ourmindai.aistudent.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.ourmindai.aistudent.model.ModelItem
import com.ourmindai.aistudent.R
import com.ourmindai.aistudent.filter.GeminiResponseFilter
import com.ourmindai.aistudent.model.AuthState
import com.ourmindai.aistudent.model.MessageModel
import com.ourmindai.aistudent.model.UserData
import com.ourmindai.aistudent.ui.theme.MicIconDark
import com.ourmindai.aistudent.ui.theme.MicIconLight
import com.ourmindai.aistudent.ui.theme.Typography
import com.ourmindai.aistudent.ui.theme.UserBgDark
import com.ourmindai.aistudent.ui.theme.UserBgLight
import com.ourmindai.aistudent.viewmodel.AuthViewModel
import com.ourmindai.aistudent.viewmodel.GeminiViewModel
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    geminiViewModel: GeminiViewModel,
    navController: NavController,
    userData: UserData,
    authViewModel: AuthViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDrawerOpen = drawerState.isOpen

    LaunchedEffect(userData.userId) {
        geminiViewModel.clearChat()
        geminiViewModel.initializeChat()
    }

    val context = LocalContext.current
    val apiError by geminiViewModel.apiError.collectAsState()

    LaunchedEffect(apiError) {
        apiError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Unauthenticate -> navController.navigate("login")
            else -> Unit
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideDrawerPage(
                drawerState = drawerState,
                onOption1Click = { Log.d("Drawer", "Option 1 clicked") },
                onOption2Click = { Log.d("Drawer", "Option 2 clicked") },
                userData = userData,
                authViewModel = authViewModel,
                geminiViewModel = geminiViewModel
            )
        }
    ) {

        ChatContentWithSideDrawer(
            modifier = modifier,
            geminiViewModel = geminiViewModel,
            isDrawerOpen = isDrawerOpen,
            onMenuClick = { scope.launch { drawerState.open() }},
            userData = userData,
        )
    }
}

@Composable
fun ChatContentWithSideDrawer(
    modifier: Modifier = Modifier,
    geminiViewModel: GeminiViewModel,
    isDrawerOpen: Boolean,
    onMenuClick: () -> Unit,
    userData: UserData
) {
    Box{
        // Main Column for chat page
        Column (
            modifier = modifier
                .then(
                    if (isDrawerOpen) Modifier.blur(16.dp) else Modifier
                )
        ){
            // Applilcation Header
            AppHeader(
                onMenuClick = onMenuClick,
                userData = userData
            )

            // List of message
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = geminiViewModel.messageList,
                geminiViewModel = geminiViewModel,
            )

            // This is the field where we s
            MessageInput(
                onMessageSend = {
                    geminiViewModel.sendMessage(it)
                },
            )
        }
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    geminiViewModel: GeminiViewModel,
) {
    val listState = rememberLazyListState()

    if(messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(
                modifier = Modifier.size(200.dp).padding(bottom = 6.dp),
                painter = painterResource(id = R.drawable.chat_icon),
                contentDescription = "Question",
                tint = Color.Unspecified
            )
             Text(text = "Welcome Buddy! ", fontSize = 17.sp, modifier = Modifier.padding(bottom = 6.dp))
             Text(text = "What can I help you?", fontSize = 22.sp)
        }
    } else  {
        LazyColumn(
            modifier = modifier,
            reverseLayout = false,  // this will start chat from top if true then start from bottom
            state = listState

        ) {
            items(messageList) { message ->
                MessageRow(messageModel = message, geminiViewModel = geminiViewModel)
            }
        }
        LaunchedEffect(messageList.size) {
            val index = messageList.indexOfLast { it.role == "model" && it.message != "Analyzing..." }
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
        }
    }
}
@Composable
fun MessageRow(messageModel: MessageModel,
               geminiViewModel: GeminiViewModel,) {
    val isModel = messageModel.role == "model"
    val isDark = isSystemInDarkTheme()
    val userBgColor = if (isDark) UserBgDark else UserBgLight

    val geminiResponseFilter = remember { GeminiResponseFilter() }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.TopStart else Alignment.TopEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 50.dp else 8.dp,
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isModel) Color.Transparent else userBgColor)
                    .padding(14.dp)
            ) {
                Column {
                    SelectionContainer {
                        when {
                            isModel && messageModel.message == "Analyzing..." -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyzing...", style = Typography.bodyLarge)
                                }
                            }
                            isModel -> {
                                InputTextFilterMessage(
                                    message = messageModel.message,
                                    geminiResponseFilter = geminiResponseFilter
                                )
                            }
                            else -> {
                                Text(
                                    text = messageModel.message,
                                    style = Typography.bodyLarge,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }

                    if (isModel && messageModel.message != "Analyzing...") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            // Copy the response
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(messageModel.message))
                                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Re-generate the response
                            IconButton(onClick = {
                                // remove the last model response
                                geminiViewModel.removeLastModelMessage()

                                // Get the last user message and sent again
                                val lastUserMessage = geminiViewModel.messageList.lastOrNull{ it.role == "user"}

                                if (lastUserMessage != null) {
                                    geminiViewModel.sendMessage(lastUserMessage.message)
                                    Toast.makeText(context, "Regenerating...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No user message to regenerate", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Refresh",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Download the text
                            DownloadWithFilePicker(message = messageModel.message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadWithFilePicker(message: String) {
    val context = LocalContext.current

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(message.toByteArray())
                Toast.makeText(context, "File saved successfully", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(context, "Error saving file", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(context, "File not saved", Toast.LENGTH_SHORT).show()
    }

    IconButton(onClick = {
        val filename = "chat_message_${System.currentTimeMillis()}.txt"
        createFileLauncher.launch(filename)
    }) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download",
            modifier = Modifier.size(18.dp)
        )
    }
}


@Composable
fun MessageInput(
    onMessageSend: (String)-> Unit,
) {
     val context = LocalContext.current
     val isDark = isSystemInDarkTheme()
     val micColor = if (isDark) MicIconDark else MicIconLight

    val model = listOf(
        ModelItem("Gemini", R.drawable.gemini),
        ModelItem("OpenAI GPT-3.5-turbo", R.drawable.chatgpt),
        ModelItem("OpenAI GPT-4o", R.drawable.chatgpt),
        ModelItem("Claude (OpenRouter)", R.drawable.claude),
        ModelItem("Mixtral 8x7B(OpenRouter)", R.drawable.claude),
    )

    // state for dorpdown menu
    var expanded by remember {
        mutableStateOf(false)
    }

    // state for selected model
    var selectedModel by remember {
        mutableStateOf(model.first())
    }

    // state for dialog
    var showSpeechToText by remember{
        mutableStateOf(false)
    }

    // state for message value for send
    var message by remember {
        mutableStateOf("")
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if(isGranted) {
            showSpeechToText = true
        }
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Toast.makeText(context, "Storage permission required", Toast.LENGTH_SHORT).show()

    }

    // Handlers
    fun checkStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {

        } else {
            storagePermissionLauncher.launch(permission)
        }
    }

    fun checkAudioPermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            showSpeechToText = true
        } else {
            audioPermissionLauncher.launch(permission)
        }
    }

    Row(
        modifier =  Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
//            placeholder = { Text(text = "Ask me anything.. ") },
            modifier = Modifier.weight(1f),   // if I type long message then it will go second line
            value = message,
            onValueChange = {
                message = it
            },

            // leadingIcon use for show the icon inside the outLinedTextField at the start
            leadingIcon = {
                IconButton(onClick = {
                    expanded = true // open dropdown
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
//                        painter = painterResource(id = selectedModel.imageRes),
                        contentDescription = "Model Selector",
                        tint = micColor
                    )
                }
            },

            // trailingIcon use for show the icon inside the outLinedTextField at the end
            trailingIcon = {
                Row (
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // File Input Button
                    IconButton(
                        onClick = {
                            checkStoragePermission()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "Attach File",
                            tint = micColor
                        )
                    }

                    // Voice Input Button
                    IconButton(onClick = {
                        Log.d("MessageInput", "Voice input clicked")
                        checkAudioPermission()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_mic_24),
                            contentDescription = "Voice Input",
                            tint = micColor
                        )
                    }
                }

            },

            label = { Text(selectedModel.name)}
        )

        // DropDown menu for model selection
        DropDownModels (
            modifier = Modifier
                .padding(0.dp),
            expanded = expanded,
            models = model,
            onSelect = {
                selectedModel = it
                expanded = false
            },
            onDismiss = {expanded = false}
        )

        // Send button
        IconButton(onClick = {
            if(message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send"
            )
        }
    }
    if (showSpeechToText) {
        SpeechToText(
            onResult = { spokenText ->
                message = spokenText // This sets the recognized text in the OutlinedTextField
                showSpeechToText = false
            },
            onDismiss = { showSpeechToText = false }
        )
    }
}

