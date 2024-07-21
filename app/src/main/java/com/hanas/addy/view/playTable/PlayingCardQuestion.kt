package com.hanas.addy.view.playTable

import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayingCardData
import com.hanas.addy.ui.samplePlayingCard
import com.hanas.addy.ui.theme.AppTheme
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun PlayingCardQuestion(
    card: PlayingCardData,
    modifier: Modifier = Modifier,
    onSelectAnswer: (Answer) -> Unit,
) {
    val brush = rememberPaperBrush()
    Column(
        modifier
            .aspectRatio(0.6f)
            .paperBackground(brush, MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        CardImageWithTitle(card, brush)
//        Attributes(card, brush)
        QuestionWithAnswers(card, brush, onSelectAnswer)
    }
}

@Composable
fun PlayingCardAttributes(
    card: PlayingCardData,
    modifier: Modifier = Modifier,
    onSelectToBattle: (Answer) -> Unit,
    ) {
    val brush = rememberPaperBrush()
    Column(
        modifier
            .aspectRatio(0.6f)
            .paperBackground(brush, MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        CardImageWithTitle(card, brush)
        Attributes(card, brush)
    }
}

@Composable
fun CardImageWithTitle(card: PlayingCardData, brush: ShaderBrush) {
    Column {
        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .paperBackground(
                    brush,
                    Color.White
                        .copy(alpha = 0.2f)
                        .compositeOver(MaterialTheme.colorScheme.primary)
                )
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            painter = painterResource(R.drawable.sample_card_image_bauhaus_imagen),
            contentDescription = null
        )
        Text(
            card.title,
            style = MaterialTheme.typography.headlineMedium,
            color = contentColorFor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Attributes(
    card: PlayingCardData,
    brush: ShaderBrush,
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
                            val path = android.graphics.Path()
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
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(Color(0xFFD7D7D7), blendMode = BlendMode.Softlight)
                        drawIntoCanvas {
                            val path = android.graphics.Path()
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
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(Color(0xFFD7D7D7), blendMode = BlendMode.Softlight)
                        drawIntoCanvas {
                            val path = android.graphics.Path()
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
            }
        }
    }
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
                    minWidth = diagonal,
                    maxWidth = diagonal,
                    minHeight = diagonal,
                    maxHeight = diagonal
                )
            )
        }

        val layoutWidth = ((2 + sqrt(3f)) * radius).toInt()
        val layoutHeight = (7 * radius / 2).toInt()
        layout(layoutWidth.toInt(), layoutHeight.toInt()) {
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
                }
            }
        }
    }
}


@Composable
fun QuestionWithAnswers(
    card: PlayingCardData,
    brush: ShaderBrush,
    onSelectAnswer: (Answer) -> Unit
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
        Column(
            Modifier
                .fillMaxSize()
                .paperBackground(brush, MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                card.question.text,
                style = MaterialTheme.typography.titleMedium,
                color = contentColorFor(MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .fillMaxWidth()
            )
            AnswersGrid(card, onSelectAnswer)
        }
    }
}

@Composable
fun AnswersGrid(card: PlayingCardData, onSelectAnswer: (Answer) -> Unit) {
    val color1 = Color(0xFF333333)
    Box(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .drawBehind {
                drawRect(Color(0xFFC8C8C8), blendMode = BlendMode.Overlay)
            }
    ) {
        Column(Modifier.padding(4.dp)) {
            Row(Modifier.weight(1f)) {
                AnswerItem(
                    answerText = card.question.a,
                    onClick = { onSelectAnswer(Answer.A) },
                    color1 = color1,
                    innerPadding = PaddingValues(end = 8.dp, bottom = 8.dp, start = 4.dp, top = 4.dp)
                )
                AnswerItem(
                    answerText = card.question.b,
                    onClick = { onSelectAnswer(Answer.B) },
                    color1 = color1,
                    innerPadding = PaddingValues(end = 4.dp, bottom = 8.dp, start = 8.dp, top = 4.dp)
                )
            }
            Row(Modifier.weight(1f)) {
                AnswerItem(
                    answerText = card.question.c,
                    onClick = { onSelectAnswer(Answer.C) },
                    color1 = color1,
                    innerPadding = PaddingValues(end = 8.dp, bottom = 4.dp, start = 4.dp, top = 8.dp)
                )
                AnswerItem(
                    answerText = card.question.d,
                    onClick = { onSelectAnswer(Answer.D) },
                    color1 = color1,
                    innerPadding = PaddingValues(end = 4.dp, bottom = 4.dp, start = 8.dp, top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RowScope.AnswerItem(
    answerText: String,
    onClick: () -> Unit,
    color1: Color,
    innerPadding: PaddingValues
) {
    AnswerBox(color1, innerPadding, onClick) {
        Text(
            answerText,
            Modifier.fillMaxWidth(),
            color = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RowScope.AnswerBox(
    color1: Color,
    innerPadding: PaddingValues,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    Box(
        Modifier
            .weight(1f)
            .fillMaxSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .drawBehind {
                drawRect(color1, blendMode = BlendMode.Overlay)
            }
            .padding(innerPadding)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun rememberPaperBrush(): ShaderBrush {
    val pattern = ImageBitmap.imageResource(R.drawable.paper_pattern)
    val patternShader = ImageShader(pattern, TileMode.Repeated, TileMode.Repeated)
    return ShaderBrush(shader = patternShader)
}

fun Modifier.paperBackground(brush: Brush, color: Color) = drawBehind {
    drawRect(brush)
    drawRect(color, blendMode = BlendMode.Overlay)
}

@Preview
@Composable
fun PlayingCardQuestionPreview() {
    AppTheme {
        Card(shape = RoundedCornerShape(24.dp)) {
            PlayingCardQuestion(samplePlayingCard, Modifier.aspectRatio(0.6f), {})
        }
    }
}

@Preview
@Composable
fun PlayingCardAttributesPreview() {
    AppTheme {
        Card(shape = RoundedCornerShape(24.dp)) {
            PlayingCardAttributes(samplePlayingCard, Modifier.aspectRatio(0.6f)) {}
        }
    }
}