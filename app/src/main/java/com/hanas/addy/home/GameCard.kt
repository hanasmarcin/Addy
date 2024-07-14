package com.hanas.addy.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.drawscope.draw
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
    description: String = "Devices that use radioactive isotopes to detect smoke",
    greenAttribute: Pair<Int, String> = Pair(10, "Green attribute description"),
    blueAttribute: Pair<Int, String> = Pair(10, "Blue attribute"),
    redAttribute: Pair<Int, String> = Pair(10, "Red attribute")
) {
    val primaryColor = Color(0xFF1F255B)
    val borderStroke = BorderStroke(8.dp, primaryColor)
    val cardElevation = CardDefaults.elevatedCardElevation()
    val cardBackgroundColor = Color(0xFFD3D4DF)

    // Extracted repeated styling into variables for better readability and maintainability
    val attributeRowModifier = Modifier
        .padding(top = 16.dp)
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .clip(RoundedCornerShape(16.dp))
        .background(primaryColor)

    val attributeValueBoxModifier = Modifier
        .padding(8.dp)
        .aspectRatio(1f, matchHeightConstraintsFirst = true)
        .clip(RoundedCornerShape(8.dp))

    val tint = Color.White.copy(0.15f)
    val tint1 = Color(0xFF6F7EB0)
    Card(
        modifier = modifier.aspectRatio(0.6f),
        shape = RoundedCornerShape(32.dp),
        elevation = cardElevation,
        border = borderStroke,
    ) {
        Column(
            Modifier
                .background(cardBackgroundColor)
                .drawPattern(R.drawable.graph_paper, tint1)
                .padding(16.dp)
        ) {
            // Content Section
            val color = Color(0xFF5C2D73)
            Column(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .drawPattern(R.drawable.charlie_brown, Color.Black.copy(0.15f))
                    .padding(8.dp)
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryColor)
                        .drawPattern(R.drawable.hideout, tint)
                ) {
                    Text(title, Modifier.padding(8.dp), style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Image(
                        painter = image,
                        contentDescription = "Game Card Image", // Added content description for accessibility
                        modifier = Modifier
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Text(description, Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp), style = MaterialTheme.typography.bodyLarge, color = Color.White)
            }

            // Attribute Rows - Refactored to use a common structure
            AttributeRow(
                modifier = attributeRowModifier.drawPattern(R.drawable.hideout, tint),
                valueBoxModifier = attributeValueBoxModifier
                    .drawBehind {
                    drawRect(Color(0xFF4CAF50), blendMode = BlendMode.Screen)
                    drawRect(Color(0xFF4CAF50), blendMode = BlendMode.Color)
                },
                attribute = greenAttribute
            )

            AttributeRow(
                modifier = attributeRowModifier.drawPattern(R.drawable.charlie_brown, tint),
                valueBoxModifier = attributeValueBoxModifier
                    .drawBehind {
                        drawRect(Color(0xFF2196F3), blendMode = BlendMode.Screen)
                        drawRect(Color(0xFF2196F3), blendMode = BlendMode.Color)
                    },
                attribute = blueAttribute
            )

            AttributeRow(
                modifier = attributeRowModifier.drawPattern(R.drawable.hexagons, tint),
                valueBoxModifier = attributeValueBoxModifier
                    .drawBehind {
                        drawRect(Color(0xFFF44336), blendMode = BlendMode.Screen)
                        drawRect(Color(0xFFF44336), blendMode = BlendMode.Color)
                    },
                attribute = redAttribute
            )
        }
    }
}

@Composable
fun GameCardBack(
    modifier: Modifier = Modifier,
) {
    val primaryColor = Color(0xFF1F255B)
    val borderStroke = BorderStroke(8.dp, primaryColor)
    val cardElevation = CardDefaults.elevatedCardElevation()
    val cardBackgroundColor = Color(0xFFD3D4DF)
    val tint = Color.White.copy(0.15f)
    val tint1 = Color(0xFF6F7EB0)
    val color = Color(0xFF5C2D73)

    Card(
        modifier = modifier.aspectRatio(0.6f),
        shape = RoundedCornerShape(32.dp),
        elevation = cardElevation,
        border = borderStroke,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(cardBackgroundColor)
                .drawPattern(R.drawable.graph_paper, tint1)
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .drawPattern(R.drawable.charlie_brown, Color.Black.copy(0.15f))
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(primaryColor)
                .drawPattern(R.drawable.hideout, tint)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(cardBackgroundColor)
                .drawPattern(R.drawable.graph_paper, tint1)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .drawPattern(R.drawable.charlie_brown, Color.Black.copy(0.15f))
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(primaryColor)
                .drawPattern(R.drawable.hideout, tint)


        )
    }
}

@Preview
@Composable
fun GameCardBackPreview() {
    AddyTheme {
        Surface {
            GameCardBack()
        }
    }
}


@Composable
fun AttributeRow(
    modifier: Modifier = Modifier,
    valueBoxModifier: Modifier = Modifier,
    attribute: Pair<Int, String>
) {
    Row(modifier) {
        Box(valueBoxModifier) {
            Text(attribute.first.toString(), Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge, color = Color.White)
        }
        Text(
            text = attribute.second,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
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