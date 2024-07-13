package com.hanas.addy.home

import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.hanas.addy.R
import com.hanas.addy.ui.theme.AddyTheme

@Composable
fun GameCardFront(
    modifier: Modifier = Modifier,
    image: Painter = painterResource(id = R.drawable.sample_fire_dog),
    title: String = "Smoke detector",
    description: String = "Devices that use radioactive isotopes to detect smoke"
) {
    val borderStroke = BorderStroke(12.dp, MaterialTheme.colorScheme.outline)
    val cardElevation = CardDefaults.elevatedCardElevation()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        elevation = cardElevation,
        border = borderStroke,
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.outline)
                .drawPattern(R.drawable.melt)
                .fillMaxSize()
        ) {
            val containerColor = MaterialTheme.colorScheme.primaryContainer
            Column(
                Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .drawContentBackground(containerColor)
            ) {
                Text(title, Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(description, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
            }
            Row(
                Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .drawContentBackground(containerColor)
            ) {
                Text(title, Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
            }
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(borderStroke.width)
//                    .clip(RoundedCornerShape(32.dp - borderStroke.width))
//            ) {
//                Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary) {
//                    Text(title, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.headlineMedium)
//                }
//                Box(
//                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.background)
//                        .padding(12.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .background(MaterialTheme.colorScheme.primaryContainer)
//                        .fillMaxWidth()
//                ) {
//                    Image(
//                        painter = image,
//                        contentDescription = null,
//                        modifier = Modifier.aspectRatio(16 / 9f).clip(RoundedCornerShape(8.dp))
//                    )
//                }
//                Column(Modifier.background(MaterialTheme.colorScheme.primary).padding(8.dp)) {
//                    Text(title, style = MaterialTheme.typography.headlineMedium)
//                    Text(description, style = MaterialTheme.typography.bodyLarge)
//                }
//            }
        }
    }
}

private fun Modifier.drawContentBackground(containerColor: Color): Modifier = drawBehind {
    drawRect(color = Color.White, blendMode = BlendMode.Difference)
//    drawRect(color = Color(0xFFFFFFFF), blendMode = BlendMode.Softlight)
    drawRect(color = Color(0xFFDDDDDD), blendMode = BlendMode.Screen)
    drawRect(color = containerColor, blendMode = BlendMode.Color)
}

private fun Modifier.drawPattern(@DrawableRes id: Int) = composed {
    val vector = ImageVector.vectorResource(id = id)
    val painter = rememberVectorPainter(image = vector)
    val tint = MaterialTheme.colorScheme.surfaceTint
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
                        draw(size = painter.intrinsicSize, colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.2f)))
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun GameCardFrontPreview() {
    AddyTheme {
        Surface {
            GameCardFront(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            )
        }
    }
}