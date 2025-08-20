package com.ourmindai.aistudent.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ourmindai.aistudent.filter.GeminiResponseFilter
import com.ourmindai.aistudent.ui.theme.Typography


@Composable
fun InputTextFilterMessage(
    message: String,
    modifier: Modifier = Modifier,
    geminiResponseFilter: GeminiResponseFilter,
    textStyle: TextStyle = Typography.bodyLarge,
    bulletColor: Color = MaterialTheme.colorScheme.primary,
    bulletSpacing: Dp = 4.dp
) {
    val textColor = MaterialTheme.colorScheme.onBackground
//    val paragraphs = remember(message) { message.split("\n") }
    val paragraphs = remember(message) {
        message.split("\n\n").map { it.trim() }.distinct().filter { it.isNotBlank() }
    }

    Column(modifier = modifier) {
        paragraphs.forEach { paragraph ->
            if(paragraph.startsWith("*")) {
                val content = paragraph.removePrefix("*").trim()
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(bottom = bulletSpacing)
                ) {
                    Text(
                        text = "•",
                        color = bulletColor,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = geminiResponseFilter.buildAnnotatedStringWithQuotes(content),
                        style = textStyle
                    )
                }
            }
            // Handle regular paragraphs with potential bold text
             else {
                Text(
                    text = geminiResponseFilter.buildAnnotatedStringWithQuotes(paragraph.trim()),
                    style = textStyle.copy(color = textColor),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
