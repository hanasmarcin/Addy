package com.hanas.addy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.model.Answer
import com.hanas.addy.model.Attribute
import com.hanas.addy.model.Attributes
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.model.Question
import com.hanas.addy.model.samplePlayCardStack
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.QuestionFace
import com.hanas.addy.view.playTable.view.cardcontent.CARD_ASPECT_RATIO
import com.hanas.addy.view.playTable.view.cardcontent.PlayCardAttributes
import com.hanas.addy.view.playTable.view.cardcontent.PlayCardQuestion

@Composable
fun CardStackPager(
    PlayCards: List<PlayCardData>,
    pagerState: PagerState = rememberPagerState { PlayCards.size },
) {
    HorizontalPager(pagerState, contentPadding = PaddingValues(horizontal = 32.dp), pageSpacing = 16.dp) { page ->
        val card = PlayCards[page]
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            var rotated by remember { mutableStateOf(false) }
            val rotation by animateFloatAsState(
                targetValue = if (rotated) 180f else 0f,
                animationSpec = tween(500), label = ""
            )
            val isFrontVisible by remember {
                derivedStateOf {
                    rotation < 90f
                }
            }
            Card(
                Modifier
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 8 * density
                    }
                    .clickable { rotated = !rotated },
                shape = RoundedCornerShape(24.dp)
            ) {
                if (isFrontVisible) {
                    PlayCardAttributes(
                        state = AttributesFace.StaticPreview,
                        card = card,
                        onSelectAttribute = {},
                        onSelectCardToBattle = {}
                    )
                } else {
                    var questionState by remember { mutableStateOf<QuestionFace>(QuestionFace.Answering) }
                    PlayCardQuestion(
                        modifier = Modifier.graphicsLayer { scaleX = -1f },
                        state = questionState,
                        card = card,
                        startAnswering = {},
                        onSelectAnswer = {
                            questionState = QuestionFace.AnswerScored(it, it == card.question.answer)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayCardBack(card: PlayCardData, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
            .aspectRatio(CARD_ASPECT_RATIO)
    )
    {
        Text(card.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.padding(4.dp))
        Text(card.question.text, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.padding(4.dp))
        AnswerRow("A", MaterialTheme.colorScheme.primary, card.question.a)
        Spacer(Modifier.padding(4.dp))
        AnswerRow("B", MaterialTheme.colorScheme.secondary, card.question.b)
        Spacer(Modifier.padding(4.dp))
        AnswerRow("C", MaterialTheme.colorScheme.tertiary, card.question.c)
        Spacer(Modifier.padding(4.dp))
        AnswerRow("D", MaterialTheme.colorScheme.primary, card.question.d)

    }
}

@Composable
fun PlayCardFront(card: PlayCardData, modifier: Modifier = Modifier) {
    Column(
        modifier
            .aspectRatio(CARD_ASPECT_RATIO)
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    )
    {
        Text(card.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.padding(4.dp))
        Text(card.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.padding(4.dp))
        AttributeRow(card.attributes.red, MaterialTheme.colorScheme.primary)
        Spacer(Modifier.padding(4.dp))
        AttributeRow(card.attributes.green, MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.padding(4.dp))
        AttributeRow(card.attributes.blue, MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
private fun AttributeRow(it: Attribute, color: Color) {
    Row(verticalAlignment = CenterVertically) {
        Text(
            text = it.value.toString(),
            color = contentColorFor(color),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .clip(BlobShape(it.value * 27f))
                .background(color)
                .layout { measurable, constraints ->
                    // Measure the text content
                    val placeable = measurable.measure(constraints)

                    // Define the blob size based on the text size
                    val blobSize = maxOf(placeable.width, placeable.height) + 8.dp.roundToPx()

                    // Layout the text within the calculated dimensions
                    layout(blobSize, blobSize) {
                        // Place the text in the center
                        placeable.placeRelative(
                            x = (blobSize - placeable.width) / 2,
                            y = (blobSize - placeable.height) / 2
                        )
                    }
                }
        )
        Text(
            text = it.name,
            modifier = Modifier.padding(4.dp), // Adjust padding as needed
        )
    }
}

@Composable
private fun AnswerRow(it: String, color: Color, description: String) {
    Row(verticalAlignment = CenterVertically) {
        Text(
            text = it,
            color = contentColorFor(color),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .clip(BlobShape())
                .background(color)
                .layout { measurable, constraints ->
                    // Measure the text content
                    val placeable = measurable.measure(constraints)

                    // Define the blob size based on the text size
                    val blobSize = maxOf(placeable.width, placeable.height) + 8.dp.roundToPx()

                    // Layout the text within the calculated dimensions
                    layout(blobSize, blobSize) {
                        // Place the text in the center
                        placeable.placeRelative(
                            x = (blobSize - placeable.width) / 2,
                            y = (blobSize - placeable.height) / 2
                        )
                    }
                }
        )
        Text(
            text = description,
            modifier = Modifier.padding(4.dp), // Adjust padding as needed
        )
    }
}

val samplePlayCard = PlayCardData(
    question = Question(
        text = "Which of these factors is NOT a significant reason why the monsoon climate is ideal for rice production in Southeast Asia?",
        a = "Heavy rainfall during the monsoon season.",
        b = "Warm temperatures throughout the year.",
        c = "A long growing season with sufficient sunlight.",
        d = "Arid conditions that allow for efficient water management.",
        answer = Answer.D,
    ),
    id = 0,
    title = "The Monsoon Whisperer",
    description = "This wise character knows the secrets of the monsoon winds and their influence on Southeast Asia's agricultural bounty.",
    imagePath = null,
    imagePrompt = "Sample image",
    attributes = Attributes(
        green = Attribute(
            name = "Climate Knowledge",
            value = 5
        ),
        blue = Attribute(
            name = "Rice production",
            value = 8
        ),
        red = Attribute(
            name = "Monsoon expertise",
            value = 3
        )
    )
)

@Preview
@Composable
fun CardStackPagerPreview() {
    AppTheme {
        CardStackPager(samplePlayCardStack.cards)
    }
}