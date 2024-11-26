package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.animateClickablePadding
import com.hanas.addy.ui.drawPattern
import com.hanas.addy.ui.samplePlayCard
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppColors.green
import com.hanas.addy.ui.theme.AppColors.pink
import com.hanas.addy.ui.theme.AppColors.yellow
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.model.QuestionFace

@Composable
fun PlayCardQuestion(
    card: PlayCardData,
    state: QuestionFace,
    modifier: Modifier = Modifier,
    startAnswering: () -> Unit,
    onSelectAnswer: (Answer) -> Unit,
) {
    Column(
        modifier
            .aspectRatio(CARD_ASPECT_RATIO)
            .background(
                green
                    .copy(alpha = 0.7f)
                    .compositeOver(MaterialTheme.colorScheme.surface)
            )
            .drawPattern(
                R.drawable.hideout,
                green
                    .copy(alpha = 0.5f)
                    .compositeOver(MaterialTheme.colorScheme.surface)
            )
            .padding(12.dp)
    ) {
        CardImageWithTitle(card)
        Spacer(Modifier.size(8.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            when (state) {
                QuestionFace.ReadyToAnswer -> {
                    PrimaryButton(
                        onClick = startAnswering,
                        modifier = Modifier
                    ) {
                        Text("Reveal the question!")
                    }
                }
                is QuestionFace.Answering, is QuestionFace.AnswerScored -> {
                    Column {
                        Text(
                            card.question.text,
                            style = MaterialTheme.typography.titleMedium,
                            color = contentColorFor(MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.orange)
                                .padding(4.dp, 2.dp, 4.dp, 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                .fillMaxWidth()
                        )
                        Spacer(Modifier.size(8.dp))
                        AnswersGrid(
                            card,
                            isCorrectAnswer = (state as? QuestionFace.AnswerScored)?.isAnswerCorrect,
                            selectedAnswer = (state as? QuestionFace.AnswerScored)?.answer,
                            onSelectAnswer = onSelectAnswer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardImageWithTitle(
    card: PlayCardData,
) {
    Column {
        card.imagePath?.let {
            AsyncImage(
                model = card.imagePath,
                contentDescription = card.imagePrompt,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(green)
                    .padding(4.dp, 2.dp, 4.dp, 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .aspectRatio(16 / 9f),
            )
            Spacer(Modifier.size(8.dp))
        }
        Text(
            card.title,
            style = MaterialTheme.typography.headlineSmall,
            color = contentColorFor(MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(green)
                .padding(4.dp, 2.dp, 4.dp, 8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface)
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
            .clip(RoundedCornerShape(8.dp))
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
    Box(Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnswerItem(
                    answerText = card.question.a + if (card.question.answer == Answer.A) " +" else "",
                    isSelected = selectedAnswer == Answer.A,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.A },
                    onClick = { onSelectAnswer(Answer.A) },
                    color = blue,
                )
                AnswerItem(
                    answerText = card.question.b + if (card.question.answer == Answer.B) " +" else "",
                    isSelected = selectedAnswer == Answer.B,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.B },
                    onClick = { onSelectAnswer(Answer.B) },
                    color = pink,
                )
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnswerItem(
                    answerText = card.question.c + if (card.question.answer == Answer.C) " +" else "",
                    onClick = { onSelectAnswer(Answer.C) },
                    color = yellow,
                    isSelected = selectedAnswer == Answer.C,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.C },
                )
                AnswerItem(
                    answerText = card.question.d + if (card.question.answer == Answer.D) " +" else "",
                    isSelected = selectedAnswer == Answer.D,
                    isCorrect = isCorrectAnswer.takeIf { selectedAnswer == Answer.D },
                    onClick = { onSelectAnswer(Answer.D) },
                    color = green,
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
    color: Color,
) {
    AnswerBox(
        isSelected = isSelected,
        isCorrect = isCorrect,
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
        color = color
    ) {
        Text(
            answerText,
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AnswerBox(
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiaryContainer,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedTransition = updateTransition(isPressed || isSelected, label = "")
    val answeredTransition = updateTransition(isCorrect, label = "")
    val containerColor by answeredTransition.animateColor(label = "") { isCorrect ->
        when (isCorrect) {
            true -> containerFor(green)
            false -> containerFor(pink)
            else -> MaterialTheme.colorScheme.surface
        }
    }

    val textColor by answeredTransition.animateColor(label = "") { isCorrect ->
        when (isCorrect) {
            true -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f).compositeOver(green)
            false -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f).compositeOver(pink)
            else -> MaterialTheme.colorScheme.onSurface
        }
    }

    val fontWeight by answeredTransition.animateInt(label = "") { isCorrect ->
        if (isCorrect != null) FontWeight.Bold.weight else FontWeight.Normal.weight
    }
    val (innerPadding, outerPadding) = pressedTransition.animateClickablePadding()
    Box(
        modifier = modifier
            .padding(outerPadding)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(innerPadding)
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .clickable(interactionSource = interactionSource, indication = ripple()) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides textColor,
            LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(fontWeight))
        ) {
            content()
        }
        var isSuccessEmoji by remember { mutableStateOf(isCorrect == true) }
        LaunchedEffect(isCorrect) {
            if (isCorrect != null)
                isSuccessEmoji = isCorrect
        }
        val emojis = remember(isSuccessEmoji) {
            if (isSuccessEmoji) correctAnswerEmojis.shuffled().take(4)
            else wrongAnswerEmojis.shuffled().take(4)
        }
        AnimatedEmoji(isSelected, -1, -1, emojis[0])
        AnimatedEmoji(isSelected, -1, 1, emojis[1])
        AnimatedEmoji(isSelected, 1, 1, emojis[2])
        AnimatedEmoji(isSelected, 1, -1, emojis[3])
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

class QuestionFacePreviewProvider : PreviewParameterProvider<QuestionFace> {
    override val values = sequenceOf(
        QuestionFace.ReadyToAnswer,
        QuestionFace.Answering,
        QuestionFace.AnswerScored(Answer.A, true),
        QuestionFace.AnswerScored(Answer.B, false)
    )

}

@Preview
@Composable
fun PlayCardQuestionsProvider(
    @PreviewParameter(QuestionFacePreviewProvider::class) state: QuestionFace
) {
    val data = samplePlayCard
    AppTheme {
        PlayCardQuestion(data, state, Modifier, {}) { }
    }
}
