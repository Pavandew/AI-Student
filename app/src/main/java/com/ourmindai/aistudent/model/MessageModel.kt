package com.ourmindai.aistudent.model

import android.graphics.Bitmap
import com.google.firebase.firestore.Exclude

data class MessageModel(
    val id: String = "",
    val message: String = "",
    val role: String = "",
    val type: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    @get:Exclude
    val bitmap: Bitmap? = null
)
