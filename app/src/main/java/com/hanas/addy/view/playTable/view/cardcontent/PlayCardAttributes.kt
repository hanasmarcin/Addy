package com.hanas.addy.view.playTable.view.cardcontent

import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.hanas.addy.R
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.playTable.model.AttributesFace
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun PlayCardAttributes(
    state: AttributesFace,
    card: PlayCardData,
    modifier: Modifier = Modifier,
    onSelectToBattle: () -> Unit,
) {
    val brush = rememberPaperBrush()
    Column(
        modifier
            .aspectRatio(0.6f)
            .paperBackground(brush, MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        CardImageWithTitle(imageHeight = Dp.Infinity, card = card, brush = brush)
        Attributes(card, state, brush, onSelectToBattle)
    }
}

@Composable
fun Attributes(
    card: PlayCardData,
    state: AttributesFace,
    brush: ShaderBrush,
    onSelectToBattle: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .paperBackground(
                brush,
                Color.White
                    .copy(alpha = 0.2f)
                    .compositeOver(MaterialTheme.colorScheme.primary)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        TripleCircleLayout(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(0.6f))
                    .fillMaxSize()
                    .aspectRatio(1f)
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(0.6f))
                    .fillMaxSize()
                    .aspectRatio(1f)
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary.copy(0.6f))
                    .fillMaxSize()
                    .aspectRatio(1f)
            )
        }
        val textStyle = MaterialTheme.typography.bodyMedium
        val context = LocalContext.current
        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            textSize = with(LocalDensity.current) { textStyle.fontSize.toPx() }
            typeface = ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible__bold)
        }

        TripleCircleLayout(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(Color(0xFFD7D7D7), blendMode = BlendMode.Softlight)
                        drawIntoCanvas {
                            val path = Path()
                            path.addArc(RectF(16.dp.toPx(), 16.dp.toPx(), size.width - 10.dp.toPx(), size.height - 10.dp.toPx()), 180f, 360f)
                            it.nativeCanvas.drawTextOnPath(card.attributes.red.name, path, 0f, 0f, paint)
                        }
                    }
                    .padding(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(card.attributes.red.value.toString(), style = MaterialTheme.typography.headlineLarge)
                if (state is AttributesFace.AddingBoost && state.boostForRed != 0) {
                    BoostText(state.boostForRed, MaterialTheme.colorScheme.primaryContainer)
                }
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(Color(0xFFD7D7D7), blendMode = BlendMode.Softlight)
                        drawIntoCanvas {
                            val path = Path()
                            path.addArc(RectF(16.dp.toPx(), 16.dp.toPx(), size.width - 10.dp.toPx(), size.height - 10.dp.toPx()), 180f, 360f)
                            it.nativeCanvas.drawTextOnPath(card.attributes.green.name, path, 0f, 0f, paint)
                        }
                    }
                    .padding(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(card.attributes.green.value.toString(), style = MaterialTheme.typography.headlineLarge)
                if (state is AttributesFace.AddingBoost && state.boostForGreen != 0) {
                    BoostText(state.boostForGreen, MaterialTheme.colorScheme.primaryContainer)
                }
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(Color(0xFFD7D7D7), blendMode = BlendMode.Softlight)
                        drawIntoCanvas {
                            val path = Path()
                            path.addArc(RectF(16.dp.toPx(), 16.dp.toPx(), size.width - 10.dp.toPx(), size.height - 10.dp.toPx()), 180f, 360f)
                            it.nativeCanvas.drawTextOnPath(card.attributes.blue.name, path, 0f, 0f, paint)
                        }
                    }
                    .padding(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(card.attributes.blue.value.toString(), style = MaterialTheme.typography.headlineLarge)
                if (state is AttributesFace.AddingBoost && state.boostForBlue != 0) {
                    BoostText(state.boostForBlue, MaterialTheme.colorScheme.primaryContainer)
                }
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onSelectToBattle)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .size(64.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
            }
        }

    }
}

@Composable
private fun BoostText(boostValue: Int, color: Color = MaterialTheme.colorScheme.primaryContainer) {
    Text(
        text = (if (boostValue > 0) "+" else "") + boostValue.toString(),
        modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(constraints.maxWidth, constraints.maxHeight) {
                placeable.placeRelative((0.7 * constraints.maxWidth).toInt(), (0.6f * placeable.height).toInt())
            }
        },
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp),
        color = contentColorFor(color).copy(alpha = 0.5f)
    )
}


@Composable
fun TripleCircleLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        val radiusFromHeight = 2 * constraints.maxHeight / 7f
        val radiusFromWidth = constraints.maxWidth / (2 + sqrt(3f))
        val radius = min(radiusFromHeight, radiusFromWidth)
        val diagonal = 2 * radius.toInt()

        val plceables = measurable.map {
            it.measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = diagonal,
                    minHeight = 0,
                    maxHeight = diagonal
                )
            )
        }

        val layoutWidth = ((2 + sqrt(3f)) * radius).toInt()
        val layoutHeight = (7 * radius / 2).toInt()
        layout(layoutWidth, layoutHeight) {
            plceables.forEachIndexed { index, placeable ->
                when (index) {
                    0 -> {
                        placeable.placeRelative((layoutWidth - diagonal) / 2, 0)
                    }
                    1 -> {
                        placeable.placeRelative(0, layoutHeight - diagonal)
                    }
                    2 -> {
                        placeable.placeRelative(layoutWidth - diagonal, layoutHeight - diagonal)
                    }
                    3 -> {
                        placeable.placeRelative((layoutWidth - placeable.width) / 2, diagonal - placeable.height / 2)
                    }
                }
            }
        }
    }
}

