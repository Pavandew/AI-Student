package com.ourmindai.aistudent.filter

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

class GeminiResponseFilter {

     fun buildAnnotatedStringWithQuotes(text: String): AnnotatedString {
        return buildAnnotatedString {
            var remainingText = text
            while (true) {
                val boldStart = remainingText.indexOf("**")
                if (boldStart == -1) {
                    append(remainingText)
                    break
                }

                // Add text before bold markers
                append(remainingText.substring(0, boldStart))

                val boldEnd = remainingText.indexOf("**", boldStart + 2)
                if (boldEnd == -1) {
                    append(remainingText.substring(boldStart))
                    break
                }

                // Add quoted text instead of bold
                append("\"")
                append(remainingText.substring(boldStart + 2, boldEnd))
                append("\"")

                remainingText = remainingText.substring(boldEnd + 2)
            }
        }
    }
}