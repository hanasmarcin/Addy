package com.hanas.addy.ui

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.min
import com.hanas.addy.ui.theme.AddyTheme
import kotlin.math.min
import kotlin.random.Random

@Composable
fun Grid(size: Size) {
    var abc by remember { mutableIntStateOf(0) }
    LocalWindowInfo.current
    BoxWithConstraints(Modifier.background(Color.Red)) {
        val cellDimension = min(maxWidth / size.width, maxHeight / size.height)
        Column {
            (0..<size.height).forEach { y ->
                Row {
                    (0..<size.width).forEach { x ->
                        Box(
                            Modifier
                                .size(cellDimension)
                                .drawBehind {
                                    drawRect(Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
                                }
                        ) {
                            if (x == 0 && y == 0) {
                                RecompositionButton(abc) { abc += 1 }
                            }
                        }
                    }
                }
            }
        }
    }
}

//class PixelGridScope {
//    @Stable
//    fun Modifier.align(alignment: Alignment.Horizontal): Modifier {
//        this.background()
//    }
//}

@Composable
fun GridLayout(size: Size) {
    var abc by remember { mutableIntStateOf(0) }
    LocalWindowInfo.current
    Layout(content = {
//        RandomColorBox(Modifier.placeInGrid(IntOffset(1, 2)))
        RandomColorBox()
        RandomColorBox()
        RandomColorBox()
    }, measurePolicy = { measurables, constraints ->
        val cellDimension = min(constraints.maxWidth / size.width, constraints.maxHeight / size.height)
        val placeables = measurables.map {
            it.measure(constraints.copy(minWidth = cellDimension, minHeight = cellDimension, maxWidth = cellDimension, maxHeight = cellDimension))
        }
        layout(constraints.minWidth, constraints.minHeight) {
            var x = 0
            placeables.forEach { placeable ->
                placeable.place(x, 0)
                x += placeable.width
            }
        }
    })
//    BoxWithConstraints(Modifier.background(Color.Red)) {
//        val cellDimension = min(maxWidth / size.width, maxHeight / size.height)
//        Column {
//            (0..<size.height).forEach { y ->
//                Row {
//                    (0..<size.width).forEach { x ->
//                        Box(
//                            Modifier
//                                .size(cellDimension)
//                                .drawBehind {
//                                    drawRect(Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
//                                }
//                        ) {
//                            if (x == 0 && y == 0) {
//                                RecompositionButton(abc) { abc += 1 }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

@Composable
private fun RandomColorBox(modifier: Modifier = Modifier) {
    Box(
        modifier
            .drawBehind {
                drawRect(Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()))
            }
    )
}

@Preview
@Composable
fun GridPreview() {
    AddyTheme {
        Surface(Modifier.fillMaxSize()) {
            Column {
                Grid(Size(4, 5))
            }
        }
    }
}

@Composable
private fun RecompositionButton(x: Int, onClick: () -> Unit) {
    Button(modifier = Modifier.wrapContentSize(), onClick = {
        onClick()
    }) {
        Text(x.toString())
    }
}

@Preview
@Composable
fun GridLayoutPreview() {
    AddyTheme {
        Surface(Modifier.fillMaxSize()) {
            GridLayout(Size(4, 6))
        }
    }
}