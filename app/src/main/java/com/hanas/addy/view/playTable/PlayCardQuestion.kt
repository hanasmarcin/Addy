package com.hanas.addy.view.playTable

import android.graphics.RectF
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.samplePlayCard
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayCardContentUiState.QuestionRace.Answering
import com.hanas.addy.view.playTable.PlayCardContentUiState.QuestionRace.Initial
import com.hanas.addy.view.playTable.PlayCardContentUiState.QuestionRace.Result
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun CardOnTableContent(
    data: PlayCardData,
    contentState: PlayCardContentUiState,
    isTransitioning: Boolean,
    clickOrigin: ClickOrigin?,
    onClickCard: () -> Unit,
    onSelectToBattle: () -> Unit,
    onSelectAnswer: (Answer) -> Unit,
    startAnswering: () -> Unit
) {
    val isClickable = isTransitioning.not() && clickOrigin != null
    val cardModifier = Modifier.clickable(enabled = isClickable) {
        onClickCard()
    }
    when (contentState) {
        is PlayCardContentUiState.AttributesDisplay -> {
            PlayCardAttributes(contentState, data, cardModifier, onSelectToBattle)
        }
        PlayCardContentUiState.BackCover -> {
            CardBack(cardModifier)
        }
        is PlayCardContentUiState.QuestionRace -> {
            PlayCardQuestion(data, contentState, cardModifier.graphicsLayer { scaleX = -1f }, startAnswering, onSelectAnswer)
        }
    }
}

@Composable
private fun CardBack(modifier: Modifier = Modifier) {
    Box(
        modifier
            .rotate(180f)
            .aspectRatio(0.6f)
            .fillMaxSize()
            .paperBackground(rememberPaperBrush(), MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painterResource(R.drawable.book_pattern_bauhaus_imagen),
            null,
            contentScale = ContentScale.Inside,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun PlayCardQuestion(
    card: PlayCardData,
    state: PlayCardContentUiState.QuestionRace,
    modifier: Modifier = Modifier,
    startAnswering: () -> Unit,
    onSelectAnswer: (Answer) -> Unit,
) {
    val brush = rememberPaperBrush()
    Column(
        modifier
            .aspectRatio(0.6f)
            .paperBackground(brush, MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        val transition = updateTransition(state, label = "")
        val imageHeight by transition.animateDp(label = "") {
            it.targetImageHeight
        }
        val imagePadding by transition.animateDp(label = "") {
            when (it) {
                Answering -> 0.dp
                Initial -> 16.dp
                is Result -> 0.dp
            }
        }
        CardImageWithTitle(imageHeight, imagePadding, card, brush)
        QuestionWithAnswers(brush) {
            when (state) {
                Initial -> {
                    AnswerBox(
                        color1 = Color(0xFF333333),
                        innerPadding = PaddingValues(end = 8.dp, bottom = 8.dp, start = 4.dp, top = 4.dp),
                        onClick = { startAnswering() },
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .weight(1f)
                            .wrapContentSize()
                            .padding(4.dp),
                    ) {
                        Text(
                            "Reveal the question!",
                            color = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                is Answering, is Result -> {
                    Text(
                        card.question.text,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColorFor(MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .fillMaxWidth()
                    )
                    AnswersGrid(
                        card,
                        isCorrectAnswer = (state as? Result)?.isAnswerCorrect,
                        selectedAnswer = (state as? Result)?.answer,
                        onSelectAnswer = onSelectAnswer
                    )
                }
            }
        }
    }
}

@Composable
fun PlayCardAttributes(
    state: PlayCardContentUiState.AttributesDisplay,
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
fun CardImageWithTitle(
    imageHeight: Dp = Dp.Infinity,
    imagePadding: Dp = 16.dp,
    card: PlayCardData,
    brush: ShaderBrush,
) {
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
                .padding(horizontal = 8.dp, vertical = imagePadding)
                .clip(RoundedCornerShape(12.dp))
                .height(imageHeight)
                .aspectRatio(16 / 9f),
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
    card: PlayCardData,
    state: PlayCardContentUiState.AttributesDisplay,
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
                if (state is PlayCardContentUiState.AttributesDisplay.AddingBoost && state.boostForRed != 0) {
                    BoostText(state.boostForRed, MaterialTheme.colorScheme.primaryContainer)
                }
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
                if (state is PlayCardContentUiState.AttributesDisplay.AddingBoost && state.boostForGreen != 0) {
                    BoostText(state.boostForGreen, MaterialTheme.colorScheme.primaryContainer)
                }
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
                if (state is PlayCardContentUiState.AttributesDisplay.AddingBoost && state.boostForBlue != 0) {
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


@Composable
fun QuestionWithAnswers(
    brush: ShaderBrush,
    content: @Composable ColumnScope.() -> Unit
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
            content()
        }
    }
}

@Composable
fun AnswersGrid(card: PlayCardData, selectedAnswer: Answer?, isCorrectAnswer: Boolean?, onSelectAnswer: (Answer) -> Unit) {
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
                    isSelected = selectedAnswer == Answer.A,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.A },
                    innerPadding = PaddingValues(end = 8.dp, bottom = 8.dp, start = 4.dp, top = 4.dp)
                )
                AnswerItem(
                    answerText = card.question.b,
                    onClick = { onSelectAnswer(Answer.B) },
                    color1 = color1,
                    isSelected = selectedAnswer == Answer.B,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.B },
                    innerPadding = PaddingValues(end = 4.dp, bottom = 8.dp, start = 8.dp, top = 4.dp)
                )
            }
            Row(Modifier.weight(1f)) {
                AnswerItem(
                    answerText = card.question.c,
                    onClick = { onSelectAnswer(Answer.C) },
                    color1 = color1,
                    isSelected = selectedAnswer == Answer.C,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.C },
                    innerPadding = PaddingValues(end = 8.dp, bottom = 4.dp, start = 4.dp, top = 8.dp)
                )
                AnswerItem(
                    answerText = card.question.d,
                    onClick = { onSelectAnswer(Answer.D) },
                    color1 = color1,
                    isSelected = selectedAnswer == Answer.D,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.D },
                    innerPadding = PaddingValues(end = 4.dp, bottom = 4.dp, start = 8.dp, top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun RowScope.AnswerItem(
    answerText: String,
    isSelected: Boolean,
    isCorrect: Boolean? = null,
    onClick: () -> Unit,
    color1: Color,
    innerPadding: PaddingValues
) {
    AnswerBox(
        color1 = color1,
        innerPadding = innerPadding,
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .padding(4.dp),
    ) {
        Text(
            answerText,
            Modifier.fillMaxWidth(),
            color = when (isCorrect) {
                true -> Color.Green
                false -> Color.Red
                null -> contentColorFor(MaterialTheme.colorScheme.tertiaryContainer)
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold.takeIf { isSelected })
        )
    }
}

@Composable
fun AnswerBox(
    color: Color = MaterialTheme.colorScheme.tertiaryContainer,
    color1: Color,
    innerPadding: PaddingValues,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .drawBehind {
                drawRect(color1, blendMode = BlendMode.Overlay)
            }
            .padding(innerPadding)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
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

class PlayCardProvider : PreviewParameterProvider<PlayCardContentUiState> {
    override val values = sequenceOf(
        PlayCardContentUiState.AttributesDisplay.Initial,
        Initial,
        Answering,
        Result(Answer.B, true),
        Result(Answer.C, false),
        PlayCardContentUiState.AttributesDisplay.AddingBoost(3, 0, -1),
        PlayCardContentUiState.AttributesDisplay.WaitingForAttributeBattle,
        PlayCardContentUiState.BackCover,
    )
}

@Preview
@Composable
fun PlayCardAnimationPreview() {
    AppTheme {
        var state by remember { mutableStateOf<PlayCardContentUiState.QuestionRace>(Initial) }
        Card(shape = RoundedCornerShape(24.dp)) {
            CardOnTableContent(samplePlayCard, state, false, null, {}, {}, {
                state = Result(it, it == Answer.A)
            }) {
                state = Answering
            }
        }
    }
}

@Preview
@Composable
fun PlayCardPreview(@PreviewParameter(PlayCardProvider::class) state: PlayCardContentUiState) {
    AppTheme {
        Card(shape = RoundedCornerShape(24.dp)) {
            CardOnTableContent(samplePlayCard, state, false, null, {}, {}, {}, {})
        }
    }
}