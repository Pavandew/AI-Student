package com.ourmindai.aistudent.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ourmindai.aistudent.R
import com.ourmindai.aistudent.ui.theme.Typography

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {

    var startAnimation by remember { mutableStateOf(false) }

    // Scale Animation
    val scale = animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0.5f,
        animationSpec = tween( 1000),
        label = "iconScale"
    )

    // Text fade-in and slide-up animation
    val alpha by animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 500),
        label = "textAlpha"
    )

    val offsetY by animateDpAsState(
        targetValue = if(startAnimation) 0.dp else 20.dp,
        animationSpec = tween(1200, delayMillis = 500),
        label = "textOffset"
    )

    // trigger the animation
    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                painter = painterResource(R.drawable.chat_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(400.dp)
                    .scale(scale.value),
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "AI Student",
                modifier = Modifier
                    .alpha(alpha)
                    .offset(y = offsetY),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = Typography.headlineLarge
            )
        }
    }
    
}