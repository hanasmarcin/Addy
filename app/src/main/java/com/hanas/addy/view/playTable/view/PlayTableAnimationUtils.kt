package com.hanas.addy.view.playTable.view

import android.util.Log
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.hanas.addy.view.playTable.model.CardRotation
import com.hanas.addy.view.playTable.model.PlayCardContentState
import com.hanas.addy.view.playTable.view.uistate.PlayCardUiPlacement


@Composable
fun animateOffset(
    transition: Transition<PlayCardUiPlacement>, screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
): State<Offset> {
    return transition.animateOffset(transitionSpec = { spec() }, label = "ccc") {
        val dpOffset = it.targetOffset(screenSizeInDp, unscaledCardSizeInDp)
        Log.d("HANASSS", "animateOffset: $dpOffset")
        Offset(dpOffset.x.value, dpOffset.y.value)
    }
}

fun <T : Any> spec() = tween<T>(500)

@Composable
fun animateWidth(
    transition: Transition<PlayCardUiPlacement>, screenSizeInDp: DpSize
) = transition.animateDp(transitionSpec = { spec() }, label = "bbb") {
    val width = it.targetWidth()
    (if (width == Dp.Infinity) screenSizeInDp.width
    else width).apply {
        Log.d(
            "HANASSS", "animateWidth: $this" +
                ""
        )
    }

}

@Composable
fun animateRotationZ(transition: Transition<PlayCardUiPlacement>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationZ().apply {
        Log.d("HANASSS", "animateRotationZ: $this")
    }
}

@Composable
fun animateOrientation(transition: Transition<PlayCardContentState>) = transition.animateValue(
    typeConverter = CardRotation.ToVector,
    transitionSpec = { spec() }, label = "aaa"
) {
    it.rotation.apply {
        Log.d("HANASSS", "animateRotation: $this")
    }
}