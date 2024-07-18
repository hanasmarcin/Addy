package com.hanas.addy.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource

fun Modifier.drawPattern(
    @DrawableRes id: Int,
    tint: Color = Color.White.copy(alpha = 0.5f)
) = composed {
    val vector = ImageVector.vectorResource(id = id)
    val painter = rememberVectorPainter(image = vector)
    drawBehind {
        val patternWidth = painter.intrinsicSize.width
        val patternHeight = painter.intrinsicSize.height

        // Calculate repetitions needed to fill the canvas
        val horizontalRepetitions = (size.width / patternWidth).toInt() + 1
        val verticalRepetitions = (size.height / patternHeight).toInt() + 1
        for (x in 0 until horizontalRepetitions) {
            for (y in 0 until verticalRepetitions) {
                with(painter) {
                    val xPos = x * patternWidth
                    val yPos = y * patternHeight
                    // Use translate to position the painter
                    translate(left = xPos, top = yPos) {
                        draw(size = painter.intrinsicSize, colorFilter = ColorFilter.tint(tint))
                    }
                }
            }
        }
    }
}

fun Modifier.drawInvertedColors(color: Color) = drawBehind {
    drawRect(Color.White, blendMode = BlendMode.Difference)
    drawRect(color, blendMode = BlendMode.Plus)
    drawRect(color, blendMode = BlendMode.Color)
}
