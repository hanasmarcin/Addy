package com.hanas.addy.view.playTable

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
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayingCardData
import com.hanas.addy.ui.samplePlayingCard
import com.hanas.addy.ui.theme.AppTheme

@Composable
fun PlayingCardQuestion(
    card: PlayingCardData,
    modifier: Modifier = Modifier,
    onSelectAnswer: (Answer) -> Unit,
) {
    val brush = rememberPaperBrush()
    val color = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val color1 = Color(0xFF333333)
    Column(
        modifier
            .aspectRatio(0.6f)
            .paperBackground(brush, color)
            .padding(16.dp)
    )
    {
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
                        .fillMaxWidth(),
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp, bottom = 8.dp, start = 8.dp, top = 0.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .drawBehind {
                            drawRect(Color(0xFFC8C8C8), blendMode = BlendMode.Overlay)
                        }
                ) {
                    Column(Modifier.padding(4.dp)) {
                        Row(Modifier.weight(1f)) {
                            AnswerBox(
                                color1,
                                PaddingValues(end = 8.dp, bottom = 8.dp, start = 4.dp, top = 4.dp),
                                { onSelectAnswer(Answer.A) }
                            ) {
                                Text(
                                    card.question.a,
                                    Modifier.fillMaxWidth(),
                                    color = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            AnswerBox(
                                color1,
                                PaddingValues(end = 4.dp, bottom = 8.dp, start = 8.dp, top = 4.dp),
                                { onSelectAnswer(Answer.B) }
                            ) {
                                Text(
                                    card.question.b,
                                    Modifier.fillMaxWidth(),
                                    color = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Row(Modifier.weight(1f)) {
                            AnswerBox(
                                color1,
                                PaddingValues(end = 8.dp, bottom = 4.dp, start = 4.dp, top = 8.dp),
                                {
                                    onSelectAnswer(Answer.C)
                                }
                            ) {
                                Text(
                                    card.question.c,
                                    Modifier.fillMaxWidth(),
                                    color = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium

                                )
                            }
                            AnswerBox(
                                color1,
                                PaddingValues(end = 4.dp, bottom = 4.dp, start = 8.dp, top = 8.dp),
                                { onSelectAnswer(Answer.D) }
                            ) {
                                Text(
                                    card.question.d,
                                    Modifier.fillMaxWidth(),
                                    color = contentColorFor(MaterialTheme.colorScheme.tertiaryContainer),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun rememberPaperBrush(): ShaderBrush {
    val pattern = ImageBitmap.imageResource(R.drawable.paper_pattern)
    val patternShader = ImageShader(pattern, TileMode.Repeated, TileMode.Repeated)
    val brush = ShaderBrush(shader = patternShader)
    return brush
}

@Composable
private fun RowScope.AnswerBox(
    color1: Color,
    innerPadding: PaddingValues,
    onclick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    Box(
        Modifier.Companion
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
            .clickable(onClick = onclick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
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