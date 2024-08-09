package com.hanas.addy.ui.components

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.view.animatePaddingValues

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    color: Color = AppColors.pink,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val (innerPadding, outerPadding) = animateClickablePadding(interactionSource)
    Box(
        modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
            .padding(outerPadding)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(innerPadding)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surface)
            .heightIn(min = 48.dp)
            .clickable(interactionSource = interactionSource, indication = rememberRipple(), onClick = onClick)
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelLarge,
            LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .graphicsLayer {
                        alpha = if (isLoading) 0f else 1f
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                with(LocalDensity.current) {
                    CircularProgressIndicator(
                        Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp()),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun animateClickablePadding(
    interactionSource: MutableInteractionSource,
): Pair<PaddingValues, PaddingValues> {
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedTransition = updateTransition(isPressed, label = "")
    return pressedTransition.animateClickablePadding()
}

@Composable
fun Transition<Boolean>.animateClickablePadding(
): Pair<PaddingValues, PaddingValues> {
    val layoutDirection = LocalLayoutDirection.current
    val innerPadding by animatePaddingValues(layoutDirection, "") { pressed ->
        if (pressed) PaddingValues(2.dp) else PaddingValues(top = 2.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
    }
    val outerPadding by animatePaddingValues(layoutDirection, "") { pressed ->
        if (pressed) PaddingValues(top = 6.dp, bottom = 0.dp, start = 2.dp, end = 2.dp) else PaddingValues(0.dp)
    }
    return innerPadding to outerPadding
}



@Preview
@Composable
fun AppButtonPreview() {
    AppTheme {
        Surface {
            Column {
                PrimaryButton(modifier = Modifier
                    .padding(32.dp), onClick = {}) {
                    Text("Button ggrsfehtrs")
                }
                PrimaryButton(modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(), onClick = {}) {
                    Text("Button ggrsfehtrs")
                }
                PrimaryButton(modifier = Modifier.padding(32.dp), isLoading = true, onClick = {}) {
                    Text("Button ggrsfehtrs")
                }
            }
        }
    }
}


