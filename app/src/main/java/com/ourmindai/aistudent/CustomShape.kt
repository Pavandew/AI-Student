package com.ourmindai.aistudent

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

object CustomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            // Start at left corner
            moveTo(w * 0.25f, h * 0.5f)
            // Upper left curve
            cubicTo(
                w * 0.25f, h * 0.2f,
                w * 0.75f, h* 0.2f,
                w * 0.75f, h * 0.5f
            )
            // Lower right curve
            cubicTo(
                w * 0.75f, h * 0.8f,
                w * 0.25f, h * 0.8f,
                w * 0.25f, h * 0.5f
            )
            close()
        }
        return Outline.Generic(path)
    }
}