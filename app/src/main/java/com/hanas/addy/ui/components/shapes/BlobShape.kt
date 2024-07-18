package com.hanas.addy.ui.components.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BlobShape(private val rotationDegrees: Float = 0f) : Shape {
    val path = Path().apply {
        moveTo(140.1f, 44.3f)
        cubicTo(153.2f, 54.3f, 165.8f, 64.4f, 170.1f, 77.3f)
        cubicTo(174.4f, 90.2f, 170.5f, 105.9f, 165.7f, 121.5f)
        cubicTo(160.9f, 137.2f, 155.3f, 152.8f, 144.2f, 159.4f)
        cubicTo(133.1f, 166.1f, 116.6f, 163.9f, 102.7f, 160.1f)
        cubicTo(88.9f, 156.3f, 77.8f, 151.1f, 64.0f, 145.3f)
        cubicTo(50.2f, 139.5f, 33.8f, 133.2f, 24.5f, 120.7f)
        cubicTo(15.2f, 108.3f, 13.1f, 89.7f, 17.8f, 72.7f)
        cubicTo(22.5f, 55.7f, 30.0f, 40.4f, 43.2f, 30.9f)
        cubicTo(56.3f, 21.5f, 72.2f, 17.2f, 97.7f, 21.2f)
        cubicTo(111.5f, 25.2f, 127.0f, 35.7f, 140.1f, 44.3f)
        close()
    }

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(path.transformToFitBounds(size, rotationDegrees))
    }
}