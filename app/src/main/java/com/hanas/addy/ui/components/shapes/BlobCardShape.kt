package com.hanas.addy.ui.components.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class BlobCardShape : Shape {
    val path = Path().apply {
        moveTo(248.5f, 13f)
        cubicTo(190.088f, 17.6858f, 140f, 13.5f, 115.5f, 7.5f)
        cubicTo(91f, 1.5f, 58.5f, -2.5f, 37f, 3f)
        cubicTo(15.5f, 8.5f, 3f, 21f, 0.5f, 38f)
        cubicTo(-2f, 55f, 7f, 75f, 10.5f, 92.5f)
        cubicTo(14f, 110f, 13.5f, 132.898f, 10.5f, 150.5f)
        cubicTo(7.5f, 168.102f, -0.5f, 178f, 0.5f, 192.5f)
        cubicTo(1.5f, 207f, 5.5f, 218.5f, 24.5f, 232f)
        cubicTo(43.5f, 245.5f, 68f, 250.5f, 101.5f, 253f)
        cubicTo(135f, 255.5f, 184.5f, 248f, 210f, 242f)
        cubicTo(235.5f, 236f, 273f, 235.5f, 294.5f, 242.5f)
        cubicTo(316f, 249.5f, 343f, 263.5f, 357.5f, 252f)
        cubicTo(372f, 240.5f, 373f, 222f, 367f, 210.5f)
        cubicTo(361f, 199f, 353.575f, 161.5f, 352f, 125.5f)
        cubicTo(350.425f, 89.4998f, 392.161f, 48.5527f, 362f, 20.5f)
        cubicTo(349.776f, 9.13084f, 337f, 5f, 318f, 2f)
        cubicTo(299f, -1f, 277.761f, 10.6525f, 248.5f, 13f)
        close()
    }

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(path.transformToFitBounds(size, 0f))
    }
}
