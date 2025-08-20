package com.ourmindai.aistudent.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ourmindai.aistudent.model.ModelItem
import com.ourmindai.aistudent.ui.theme.Typography
import kotlin.collections.forEach


@Composable
fun DropDownModels(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    models: List<ModelItem>,
    onSelect: (ModelItem) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier= modifier.padding(0.dp)
    ) {
        models.forEach { model ->
            DropdownMenuItem(
//                modifier = Modifier.background(BackGroundColor),
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Icon for Model Image
                        Box(
                            modifier = Modifier
                                .size(34.dp) // width and height are equal
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                painter = painterResource(id = model.imageRes),
                                contentDescription = model.name,
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.Center),
                                tint = Color.Unspecified
                            )
                        }

                        // Text for Model Name
                        Text(text = model.name,
                            modifier = Modifier.padding(start = 6.dp),
                            style = Typography.bodyMedium
                        )
                    }
                },
                onClick = { onSelect(model) }
            )
        }
    }
}