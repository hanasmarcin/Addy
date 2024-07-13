package com.hanas.addy.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.R
import com.hanas.addy.ui.theme.AddyTheme

@Composable
fun GameCardFront(
    modifier: Modifier = Modifier,
    image: Painter = painterResource(id = R.drawable.sample_fire_dog),
    title: String = "Smoke detector",
    description: String = "Devices that use radioactive isotopes to detect smoke"
) {
    val borderStroke = BorderStroke(8.dp, Color(0xFF253659))
    val cardElevation = CardDefaults.elevatedCardElevation()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        elevation = cardElevation,
        border = borderStroke,
    ) {
        Column(
            Modifier
                .background(Color(0xFF03A696))
                .drawPattern(R.drawable.graph_paper)
                .padding(16.dp)

        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFBF665E))
                    .drawPattern(R.drawable.charlie_brown, Color.Black.copy(0.1f))
                    .padding(8.dp)
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF253659))
                        .drawPattern(R.drawable.hideout, Color.White.copy(0.1f))
                ) {
                    Text(title, Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Text(description, Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp), style = MaterialTheme.typography.bodyLarge, color = Color.White)
            }
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF253659))
                    .drawPattern(R.drawable.hideout, Color.White.copy(0.1f))
                    .padding(8.dp)
            ) {
                Box(
                    Modifier
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF4CAF50))
                        .padding(8.dp)
                ) {
                    Text("10", Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
                Text(
                    text = "Green attribute description",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge, color = Color.White
                )
            }
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF253659))
                    .drawPattern(R.drawable.charlie_brown, Color.White.copy(0.1f))
                    .padding(8.dp)
            ) {
                Box(
                    Modifier
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2196F3))
                        .padding(8.dp)
                ) {
                    Text("10", Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
                Text(
                    "Blue attribute",
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), style = MaterialTheme.typography.bodyLarge,
                    color = Color.White

                )
            }
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF253659))
                    .drawPattern(R.drawable.hexagons, Color.White.copy(0.1f))
                    .padding(8.dp)
            ) {
                Box(
                    Modifier
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF44336))
                        .padding(8.dp)
                ) {
                    Text("10", Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
                Text(
                    "Red attribute",
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

private fun Modifier.drawContentBackground(containerColor: Color): Modifier = drawBehind {
    drawRect(color = Color.White, blendMode = BlendMode.Difference)
    drawRect(color = Color(0xFFDDDDDD), blendMode = BlendMode.Screen)
    drawRect(color = containerColor, blendMode = BlendMode.Color)
}

private fun Modifier.drawPattern(
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


@Preview
@Composable
fun GameCardFrontPreview() {
    AddyTheme {
        Surface {
            GameCardFront(
                Modifier
                    .padding(16.dp)
            )
        }
    }
}