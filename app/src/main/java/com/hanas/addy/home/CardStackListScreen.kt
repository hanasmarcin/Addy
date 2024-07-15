@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.ui.AppTheme
import kotlinx.serialization.Serializable

@Serializable
object CardStackList : NavScreen, NavAction


fun NavGraphBuilder.cardStackListComposable(navigate: Navigate) {
    composable<CardStackList> {
        CardStackListScreen(navigate)
    }
}

@Composable
private fun CardStackListScreen(navigate: Navigate) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .drawPattern(R.drawable.graph_paper, tint = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)),
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = { Text("Card stacks") },
                navigationIcon = {
                    FilledIconButton(
                        shape = BlobShape(),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        onClick = { navigate(GoBack) },
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* ... */ },
                shape = BlobShape(),
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Add, null)
            }
        }) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.stacks_of_cards),
                    contentDescription = null
                )
                Text("You have no card stacks yet. You can create them by uploading photos of material that you want to base them on.")
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Create new stack")
                }
            }
        }
    }
}

class BlobShape : Shape {
    val path = Path().apply {
        moveTo(140.1f, 44.3f)
        cubicTo(153.2f, 54.3f, 165.8f, 64.4f, 170.1f, 77.3f)
        cubicTo(174.4f, 90.2f, 170.5f, 105.9f, 165.7f, 121.5f)
        cubicTo(160.9f, 137.2f, 155.3f, 152.8f, 144.2f, 159.4f)
        cubicTo(133.1f, 166.1f, 116.6f, 163.9f, 102.7f, 160.1f)
        cubicTo(88.9f, 156.3f, 77.8f, 151.1f, 64.0f, 145.3f)
        cubicTo(50.2f, 139.5f, 33.8f, 133.2f, 24.5f, 120.7f)
        cubicTo(15.2f, 108.3f, 13.1f, 89.7f, 17.8f, 72.7f)
        cubicTo(22.5f, 55.7f, 30.0f, 40.4f, 43.2f, 30.9f)
        cubicTo(56.3f, 21.5f, 72.2f, 17.2f, 97.7f, 21.2f)
        cubicTo(111.5f, 25.2f, 127.0f, 35.7f, 140.1f, 44.3f)
        close()
    }

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(path.transformToFitBounds(size))
    }
}

fun Path.transformToFitBounds(size: Size): Path {
    val pathBounds = getBounds()

    val scaleX = size.width / pathBounds.width
    val scaleY = size.height / pathBounds.height

    return Path().apply {
        addPath(this@transformToFitBounds) // Add the original path
        transform(Matrix().apply { scale(scaleX, scaleY) })
        translate(Offset(-pathBounds.left * scaleX, -pathBounds.top * scaleY)) // Translate
    }
}

@Preview
@Composable
fun CardStackListScreenPreview() {
    AppTheme {
        CardStackListScreen {}
    }
}