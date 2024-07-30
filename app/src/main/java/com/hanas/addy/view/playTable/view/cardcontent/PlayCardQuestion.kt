package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.playTable.model.QuestionFace

@Composable
fun PlayCardQuestion(
    card: PlayCardData,
    state: QuestionFace,
    modifier: Modifier = Modifier,
    startAnswering: () -> Unit,
    onSelectAnswer: (Answer) -> Unit,
) {
    val brush = rememberPaperBrush()
    Column(
        modifier
            .aspectRatio(CARD_ASPECT_RATIO)
            .paperBackground(brush, MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        val transition = updateTransition(state, label = "")
        val imagePadding by transition.animateDp(label = "") {
            when (it) {
                QuestionFace.Answering -> 0.dp
                QuestionFace.ReadyToAnswer -> 16.dp
                is QuestionFace.AnswerScored -> 0.dp
            }
        }
        CardImageWithTitle(imagePadding, card, brush)
        QuestionWithAnswers(brush) {
            AnimatedContent(state, label = "") {
                when (it) {
                    QuestionFace.ReadyToAnswer -> {
                        AnswerBox(
                            color1 = Color(0xFF333333),
                            innerPadding = PaddingValues(end = 8.dp, bottom = 8.dp, start = 4.dp, top = 4.dp),
                            onClick = { startAnswering() },
                            modifier = Modifier
                                .align(CenterHorizontally)
                                .animateContentSize()
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
                    is QuestionFace.Answering, is QuestionFace.AnswerScored -> {
                        Column {
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
                                isCorrectAnswer = (state as? QuestionFace.AnswerScored)?.isAnswerCorrect,
                                selectedAnswer = (state as? QuestionFace.AnswerScored)?.answer,
                                onSelectAnswer = onSelectAnswer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardImageWithTitle(
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
            .animateContentSize()
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
                .animateContentSize()
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

