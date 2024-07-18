package com.hanas.addy.ui.components.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path

fun Path.transformToFitBounds(size: Size, rotationDegrees: Float): Path {
    val path = Path().apply {
        addPath(this@transformToFitBounds)
        transform(
            Matrix().apply {
                if (rotationDegrees != 0f) {
                    rotateZ(rotationDegrees)
                }
            }
        )
    }
    val pathBounds = path.getBounds()

    val scaleX = size.width / pathBounds.width
    val scaleY = size.height / pathBounds.height

    return Path().apply {
        addPath(path) // Add the original path
        transform(Matrix().apply { scale(scaleX, scaleY) })
        translate(Offset(-pathBounds.left * scaleX, -pathBounds.top * scaleY)) // Translate
    }
}
