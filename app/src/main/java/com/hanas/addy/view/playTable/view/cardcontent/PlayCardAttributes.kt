package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hanas.addy.R
import com.hanas.addy.model.Attribute
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.components.animateClickablePadding
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.drawPattern
import com.hanas.addy.ui.samplePlayCard
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppColors.green
import com.hanas.addy.ui.theme.AppColors.pink
import com.hanas.addy.ui.theme.AppColors.yellow
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.model.AttributesFace
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun PlayCardAttributes(
    state: AttributesFace,
    card: PlayCardData,
    modifier: Modifier = Modifier,
    onSelectCardToBattle: () -> Unit,
    onSelectAttribute: (Int) -> Unit,
) {
    var targetColor by remember { mutableStateOf(Color.Transparent) }
    LaunchedEffect(state) {
        if (state is AttributesFace.BattleResult) {
            targetColor = if (state.isWon) Color.Green else Color.Red
        }
    }
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
        Attributes(card, state, onSelectAttribute, onSelectCardToBattle)
    }
//    Box(
//        Modifier
//            .aspectRatio(CARD_ASPECT_RATIO)
//            .background(color)
//    )
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.confetti_2)
    )
    val progress by animateLottieCompositionAsState(
        isPlaying = state is AttributesFace.BattleResult && state.isWon,
        composition = composition,
        iterations = 1,
    )
    if (state is AttributesFace.BattleResult && state.isWon) {
        LottieAnimation(
            composition,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(CARD_ASPECT_RATIO),
            progress = { progress },
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun Attributes(
    card: PlayCardData,
    state: AttributesFace,
    onSelectAttribute: (Int) -> Unit,
    onSelectToBattle: () -> Unit,
) {
    Spacer(Modifier.size(8.dp))
    Row {
        Text(
            if (state is AttributesFace.ChooseActiveAttribute) "Select active attribute" else card.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(green)
                .padding(4.dp, 2.dp, 4.dp, 8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
        )
        PlayButton(state is AttributesFace.ChoosingToBattle, Modifier.weight(1f), onSelectToBattle)
    }
    Spacer(Modifier.size(8.dp))
//    Column(
//        Modifier
//            .fillMaxSize()
//            .clip(RoundedCornerShape(8.dp))
//            .background(green)
//            .padding(8.dp)
//            .clip(RoundedCornerShape(12.dp))
//    ) {
    // Create attribute rows using the function
    AttributeRow(
        attribute = card.attributes.red,
        state = state,
        booster = (state as? AttributesFace.AddingBoost)?.boostForRed,
        isSelectedAsActive = (state as? AttributesFace.ActiveAttributeSelected)?.attribute == "red",
        color = pink,
    ) { onSelectAttribute(0) }
    Spacer(Modifier.size(8.dp))
    AttributeRow(
        attribute = card.attributes.green,
        state = state,
        booster = (state as? AttributesFace.AddingBoost)?.boostForGreen,
        isSelectedAsActive = (state as? AttributesFace.ActiveAttributeSelected)?.attribute == "green",
        color = blue
    ) { onSelectAttribute(1) }
    Spacer(Modifier.size(8.dp))
    AttributeRow(
        attribute = card.attributes.blue,
        state = state,
        booster = (state as? AttributesFace.AddingBoost)?.boostForBlue,
        isSelectedAsActive = (state as? AttributesFace.ActiveAttributeSelected)?.attribute == "blue",
        color = yellow
    ) { onSelectAttribute(2) }
//    }
}

@Composable
private fun PlayButton(enabled: Boolean, modifier: Modifier, onSelectToBattle: () -> Unit) {
    Box(
        modifier
            .requiredSize(50.dp)
            .clip(BlobShape(60f))
            .background(AppColors.orange)
            .padding(4.dp, 2.dp, 4.dp, 8.dp)
            .clip(BlobShape(60f))
            .background(containerFor(AppColors.orange))
            .clickable(enabled) { onSelectToBattle() }

    ) {
        Icon(Icons.Rounded.PlayArrow, null, Modifier.fillMaxSize(), tint = AppColors.orange)
    }
}

@Composable
fun AttributeRow(
    attribute: Attribute,
    state: AttributesFace,
    booster: Int?,
    isSelectedAsActive: Boolean,
    color: Color = pink,
    onSelectAttribute: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedTransition = updateTransition(isPressed || isSelectedAsActive, label = "")
    val containerColor by pressedTransition.animateColor(label = "") { pressed ->
        if (pressed) containerFor(color) else MaterialTheme.colorScheme.surface
    }
    val fontWeight by pressedTransition.animateInt(label = "") { pressed ->
        if (pressed) FontWeight.Bold.weight else FontWeight.Normal.weight
    }
    val attributeValueColor by pressedTransition.animateColor(label = "") { pressed ->
        if (pressed) color else color
            .copy(alpha = 0.7f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    }
    val (innerPadding, outerPadding) = pressedTransition.animateClickablePadding()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(outerPadding)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(innerPadding)
            .heightIn(min = 48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                enabled = state is AttributesFace.ChooseActiveAttribute,
            ) { onSelectAttribute() }
            .clip(RoundedCornerShape(6.dp))
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AttributeValue(
            attribute.value,
            booster,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(fontWeight)),
            color = attributeValueColor
        )
        Spacer(Modifier.size(2.dp))
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(2.dp))
                .background(containerColor)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(attribute.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(fontWeight)))
        }
    }
}

@Composable
private fun AttributeValue(value: Int, booster: Int?, style: TextStyle, color: Color) {
    var targetValue by remember { mutableIntStateOf(value) }
    var targetColor by remember { mutableStateOf(Color.Black) }
    LaunchedEffect(booster) {
        if (booster != null && booster != 0) {
            delay(500)
            targetValue += booster
            targetColor = (if (booster > 0) Color.Green else Color.Red).copy(alpha = 0.6f).compositeOver(Color.Black)
        }
    }
    val aaa by animateIntAsState(targetValue, label = "", animationSpec = tween(510 * abs(booster ?: 0) + 1, easing = LinearEasing))
    val target by animateColorAsState(targetColor, label = "", animationSpec = tween(500 * abs(booster ?: 0) + 1))
    val spec = tween<IntOffset>(abs(500), easing = LinearEasing)
    AnimatedContent(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(2.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        targetState = aaa,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically(spec) { -it } togetherWith slideOutVertically(spec) { it }
            } else {
                slideInVertically(spec) { it } togetherWith slideOutVertically(spec) { -it }
            }
        }, label = ""
    ) { count ->
        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(
                count.toString(),
                style = style,
                color = target,
                textAlign = TextAlign.Center,
            )
        }
    }
}

class AttributesFacePreviewProvider : PreviewParameterProvider<AttributesFace> {
    override val values = sequenceOf(
        AttributesFace.StaticPreview,
        AttributesFace.AddingBoost(1, 2, 3),
        AttributesFace.AddingBoost(-1, 0, -3),
        AttributesFace.ChooseActiveAttribute,
        AttributesFace.WaitingForActiveAttributeSelected,
        AttributesFace.ActiveAttributeSelected("green"),
        AttributesFace.BattleResult(true),
        AttributesFace.BattleResult(false),
    )

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

@Preview
@Composable
fun LottiePreview() {
    AppTheme {
        Surface {
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.confetti_2)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
            )
            LottieAnimation(composition, progress = { progress })
            var progressState by remember { mutableStateOf(0f) }
            val progress2 by animateFloatAsState(progressState, animationSpec = tween(1000))
            LaunchedEffect(Unit) {
                progressState = 1f
                delay(2000)
                progressState = 0f
            }

            Text(progress2.toString())
        }
    }
}