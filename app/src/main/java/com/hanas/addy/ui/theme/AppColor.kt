package com.hanas.addy.ui.theme

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

object AppColors {
    private const val SURFACE_TINT_ALPHA = 0.2f
    val yellow = Color(0xFFE9B824)
    val orange = Color(0xFFEE9322)
    val pink = Color(0xFFD8686E)
    val blue = Color(0xFF219EBC)
    val green = Color(0xFF207F62)

    @Composable
    fun containerFor(color: Color) = color.copy(alpha = SURFACE_TINT_ALPHA).compositeOver(colorScheme.surface)
}