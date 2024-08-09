package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.random.Random

val correctAnswerEmojis = listOf("\uD83C\uDF89", "\uD83C\uDF8A", "\uD83C\uDFC6", "\uD83E\uDD73", "\uD83D\uDCAA", "\uD83C\uDF1F")
val wrongAnswerEmojis = listOf("\u274C", "\uD83D\uDE1E", "\uD83D\uDEAB", "\uD83D\uDC94", "\uD83D\uDE14")

@Preview
@Composable
fun AnimatableEmoji() {
    var showEmoji by remember { mutableStateOf(false) }

    Box(
        Modifier.padding(64.dp),
    ) {
        Button(onClick = { showEmoji = !showEmoji }) {
            Text("Click me!")
        }
        AnimatedEmoji(showEmoji, -1, -1)
        AnimatedEmoji(showEmoji, -1, 1)
        AnimatedEmoji(showEmoji, 1, 1)
        AnimatedEmoji(showEmoji, 1, -1)
    }
}

@Composable
fun AnimatedEmoji(
    showEmoji: Boolean,
    directionX: Int,
    directionY: Int,
    emoji: String = "😄",
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.7f) }
    val offsetX = remember { Animatable(10f) }
    val offsetY = remember { Animatable(5f) }
    LaunchedEffect(showEmoji) {
        if (showEmoji) {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600, easing = randomBezier())
                )
                scale.animateTo(
                    targetValue = 0.7f,
                    animationSpec = tween(delayMillis = 200, durationMillis = 400, easing = randomBezier())
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500, easing = randomBezier())
                )
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(delayMillis = 200, durationMillis = 500, easing = randomBezier())
                )
            }
            val offsetBezier = randomBezier()
            launch {
                offsetX.animateTo(
                    targetValue = 80.toFloat(),
                    animationSpec = tween(durationMillis = 1200, easing = offsetBezier)
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 40.toFloat(),
                    animationSpec = tween(durationMillis = 1200, easing = offsetBezier)
                )
            }
        } else { // Reset animation when hidden
            scale.snapTo(0.7f)
            alpha.snapTo(0f)
            offsetX.snapTo(10f)
            offsetY.snapTo(5f)
        }
    }
    Text(
        text = emoji,
        fontSize = 32.sp,
        modifier = Modifier
            .offset {
                IntOffset(
                    directionX * offsetX.value.dp.roundToPx(),
                    directionY * offsetY.value.dp.roundToPx()
                )
            }
            .graphicsLayer {
                transformOrigin = TransformOrigin.Center
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
    )
}

private fun randomBezier() = CubicBezierEasing(0f, 0f, Random.nextFloat() * 0.8f + 0.1f, 1f).let {
    Easing { t ->
        val adjustedT = t.coerceIn(0.01f, 0.99f)
        it.transform(adjustedT)
    }
}