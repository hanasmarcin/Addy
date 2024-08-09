package com.hanas.addy.view.playTable.view

import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.hanas.addy.view.playTable.model.CardRotation
import com.hanas.addy.view.playTable.model.PlayCardContentState
import com.hanas.addy.view.playTable.view.uistate.PlayCardUiPlacement


@Composable
fun animateOffset(
    transition: Transition<PlayCardUiPlacement>, screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
): State<Offset> {
    return transition.animateOffset(transitionSpec = { spec() }, label = "ccc") {
        val dpOffset = it.targetOffset(screenSizeInDp, unscaledCardSizeInDp)
        Offset(dpOffset.x.value, dpOffset.y.value)
    }
}

fun <T : Any> spec() = tween<T>(500)

@Composable
fun animateWidth(
    transition: Transition<PlayCardUiPlacement>, screenSizeInDp: DpSize
) = transition.animateDp(transitionSpec = { spec() }, label = "bbb") {
    val width = it.targetWidth()
    if (width == Dp.Infinity) screenSizeInDp.width
    else width
}

@Composable
fun animateRotationZ(transition: Transition<PlayCardUiPlacement>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationZ()
}

@Composable
fun animateOrientation(transition: Transition<PlayCardContentState>) = transition.animateValue(
    typeConverter = CardRotation.ToVector,
    transitionSpec = { spec() }, label = "aaa"
) {
    it.rotation
}

@Composable
fun <T> Transition<T>.animatePaddingValues(
    layoutDirection: LayoutDirection,
    label: String,
    targetValueByState: @Composable (state: T) -> PaddingValues
) = animateValue(
    typeConverter = object : TwoWayConverter<PaddingValues, AnimationVector4D> {
        override val convertFromVector: (AnimationVector4D) -> PaddingValues
            get() = {
                PaddingValues(
                    it.v1.coerceAtLeast(0f).dp,
                    it.v2.coerceAtLeast(0f).dp,
                    it.v3.coerceAtLeast(0f).dp,
                    it.v4.coerceAtLeast(0f).dp
                )
            }
        override val convertToVector: (PaddingValues) -> AnimationVector4D
            get() = {
                AnimationVector4D(
                    it.calculateLeftPadding(layoutDirection).value,
                    it.calculateTopPadding().value,
                    it.calculateRightPadding(layoutDirection).value,
                    it.calculateBottomPadding().value
                )
            }

    },
    label = label,
    targetValueByState = targetValueByState
)

@Composable
fun animatePaddingValuesAsState(targetValue: PaddingValues, layoutDirection: LayoutDirection, label: String) = animateValueAsState(
    targetValue = targetValue,
    typeConverter = object : TwoWayConverter<PaddingValues, AnimationVector4D> {
        override val convertFromVector: (AnimationVector4D) -> PaddingValues
            get() = {
                PaddingValues(
                    it.v1.coerceAtLeast(0f).dp,
                    it.v2.coerceAtLeast(0f).dp,
                    it.v3.coerceAtLeast(0f).dp,
                    it.v4.coerceAtLeast(0f).dp
                )
            }
        override val convertToVector: (PaddingValues) -> AnimationVector4D
            get() = {
                AnimationVector4D(
                    it.calculateLeftPadding(layoutDirection).value,
                    it.calculateTopPadding().value,
                    it.calculateRightPadding(layoutDirection).value,
                    it.calculateBottomPadding().value
                )
            }

    }, label = label
)