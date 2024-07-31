package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hanas.addy.model.Attribute
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.samplePlayCard
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.model.AttributesFace
import kotlin.math.abs

@Composable
fun PlayCardAttributes(
    state: AttributesFace,
    card: PlayCardData,
    modifier: Modifier = Modifier,
    onSelectCardToBattle: () -> Unit,
    onSelectAttribute: (Int) -> Unit,
) {
    val brush = rememberPaperBrush()
    var targetColor by remember { mutableStateOf(Color.Transparent) }
    LaunchedEffect(state) {
        if (state is AttributesFace.BattleResult) {
            targetColor = if (state.isWon) Color.Green else Color.Red
        }
    }
    val color by animateColorAsState(targetColor, label = "", animationSpec = tween(2000))
    Column(
        modifier
            .aspectRatio(CARD_ASPECT_RATIO)
            .paperBackground(brush, MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        CardImageWithTitle(card = card, brush = brush)
        Attributes(card, state, brush, onSelectAttribute, onSelectCardToBattle)
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(color)
    )
}

@Composable
fun Attributes(
    card: PlayCardData,
    state: AttributesFace,
    brush: ShaderBrush,
    onSelectAttribute: (Int) -> Unit,
    onSelectToBattle: () -> Unit,
) {
    Column(
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
        Text(
            if (state is AttributesFace.ChooseActiveAttribute) "Select active attribute" else card.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(Modifier.size(16.dp))

        // Create attribute rows using the function
        AttributeRow(card.attributes.red, state, (state as? AttributesFace.AddingBoost)?.boostForRed) { onSelectAttribute(0) }
        Spacer(Modifier.size(16.dp))
        AttributeRow(card.attributes.green, state, (state as? AttributesFace.AddingBoost)?.boostForGreen) { onSelectAttribute(1) }
        Spacer(Modifier.size(16.dp))
        AttributeRow(card.attributes.blue, state, (state as? AttributesFace.AddingBoost)?.boostForBlue) { onSelectAttribute(2) }
        Spacer(Modifier.size(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .paperBackground(
                    brush,
                    MaterialTheme.colorScheme.primaryContainer
                )
                .clickable(state is AttributesFace.ChoosingToBattle) { onSelectToBattle() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Select to battle")
        }
    }
}

@Composable
fun AttributeRow(attribute: Attribute, state: AttributesFace, booster: Int?, onSelectAttribute: () -> Unit) {
    val brush = rememberPaperBrush()

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .paperBackground(brush, MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(state is AttributesFace.ChooseActiveAttribute) { onSelectAttribute() }
            .clip(RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AttributeValue(
            attribute.value,
            booster,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.size(8.dp))
        Text(attribute.name)
    }
}


@Composable
private fun AttributeValue(value: Int, booster: Int?, style: TextStyle) {
    var targetValue by remember { mutableIntStateOf(value) }
    var targetColor by remember { mutableStateOf(Color.Black) }
    LaunchedEffect(booster) {
        if (booster != null && booster != 0) {
            targetValue += booster
            targetColor = (if (booster > 0) Color.Green else Color.Red).copy(alpha = 0.6f).compositeOver(Color.Black)
        }
    }
    val aaa by animateIntAsState(targetValue, label = "", animationSpec = tween(510 * abs(booster ?: 0) + 1, easing = LinearEasing))
    val color by animateColorAsState(targetColor, label = "", animationSpec = tween(500 * abs(booster ?: 0) + 1))
    val spec = tween<IntOffset>(abs(500), easing = LinearEasing)
    AnimatedContent(
        targetState = aaa,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically(spec) { -it } togetherWith slideOutVertically(spec) { it }
            } else {
                slideInVertically(spec) { it } togetherWith slideOutVertically(spec) { -it }
            }
        }, label = ""
    ) { count ->
        Text(
            count.toString(),
            style = style,
            color = color,
            textAlign = TextAlign.Center,
        )
    }
}

class AttributesFacePreviewProvider : PreviewParameterProvider<AttributesFace> {
    override val values = sequenceOf(
        AttributesFace.StaticPreview,
        AttributesFace.AddingBoost(1, 2, 3),
        AttributesFace.AddingBoost(-1, 0, -3),
        AttributesFace.ChooseActiveAttribute,
        AttributesFace.WaitingForAttributeBattle,
        AttributesFace.BattleResult(true),
        AttributesFace.BattleResult(false),
    )

}

@Preview
@Composable
fun CounterPreview() {
    AppTheme {
        Surface {
            AttributeValue(2, 3, MaterialTheme.typography.headlineLarge)
        }
    }
}

@Preview
@Composable
fun PlayCardAttributesPreview(
    @PreviewParameter(AttributesFacePreviewProvider::class) state: AttributesFace
) {
    val data = samplePlayCard
    AppTheme {
        PlayCardAttributes(state, data, onSelectCardToBattle = {}) { }
    }
}
