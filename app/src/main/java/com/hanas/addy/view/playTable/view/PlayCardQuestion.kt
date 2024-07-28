package com.hanas.addy.view.playTable.view

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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.samplePlayCard
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.model.PlayCardContentUiState
import com.hanas.addy.view.playTable.model.PlayCardContentUiState.QuestionRace.Answering
import com.hanas.addy.view.playTable.model.PlayCardContentUiState.QuestionRace.Initial
import com.hanas.addy.view.playTable.model.PlayCardContentUiState.QuestionRace.Result

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
    color1: Color,
    innerPadding: PaddingValues,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiaryContainer,
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
            CardOnTableContent(samplePlayCard, state, false, {}, {}, {
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
            CardOnTableContent(samplePlayCard, state, false, {}, {}, {}, {})
        }
    }
}