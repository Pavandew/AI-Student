package com.ourmindai.aistudent.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale


@Composable
fun SpeechToText(
    onResult: (String) -> Unit,
    onDismiss: () -> Unit,
//    start: Boolean,
//    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(false)
    }

    var shouldCheckSpeech by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if(isGranted) {
            shouldCheckSpeech = true
        } else {
            Toast.makeText(context, "Permission to record audio is required", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    // Request permission to record audio
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.RECORD_AUDIO
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            permissionLauncher.launch(permission)
        } else {
            permissionGranted = true
            shouldCheckSpeech = true
        }
    }

    if(!permissionGranted && shouldCheckSpeech) {
        // Wait for the permission result
//        LaunchedEffect(Unit) {
            Toast.makeText(context, "Speech not available on this device", Toast.LENGTH_SHORT).show()
            onDismiss()
//        }
        return
    }

    // Create an intent for speech recognition
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if(spokenText!= null) {
                Log.d("SpeechToText", "Recognized speech: $spokenText")
                onResult(spokenText)
            } else {
                Log.d("SpeechToText", "No speech recognized")
                onDismiss()
            }
        } else {
            Log.d("SpeechToText", "Speech recognition failed or was cancelled")
            onDismiss()
        }
    }
    LaunchedEffect(Unit) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something...")
        }
        launcher.launch(intent)
    }
}