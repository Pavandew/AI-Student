package com.ourmindai.aistudent.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ourmindai.aistudent.R
import com.ourmindai.aistudent.model.UserData
import com.ourmindai.aistudent.ui.theme.Typography

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    userData: UserData
) {
    Row (
        modifier = Modifier
            .fillMaxWidth(),
//            .background(HeaderColor),
//            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick
        ) {
            Icon(imageVector = Icons.Default.Menu,
                contentDescription = "menu")
        }
        Text(
            modifier = Modifier.padding(start = 10.dp,),
            text = "AI Student",
            style = Typography.headlineMedium,
        )

        Spacer(modifier = Modifier.weight(1f))

        AsyncImage(
            model = userData.profilePictureUrl  ?: R.drawable.chat_icon,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
