@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.components.AppButton
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object CardStackList : NavScreen


fun NavGraphBuilder.cardStackListComposable(navHandler: NavigationHandler) {
    composable<CardStackList> {
        val viewModel: CardStackListViewModel = koinNavViewModel()
        val cardStacks by viewModel.cardStacksFlow.collectAsState()
        CardStackListScreen(
            cardStacks = cardStacks,
            navHandler = navHandler,
        )
    }
}

@Composable
private fun CardStackListScreen(
    cardStacks: List<PlayingCardStack>,
    navHandler: NavigationHandler,
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .drawPattern(R.drawable.graph_paper, tint = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f)),
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.card_stack_list_screen_title)) }, navigationIcon = {
                FilledIconButton(
                    shape = BlobShape(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    onClick = { navHandler.navigate(GoBack, true) },
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            }, colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navHandler.navigate(CreateNewCardStack, true) },
                shape = BlobShape(30f),
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
            if (cardStacks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.stacks_of_cards), contentDescription = null
                    )
                    Text(stringResource(R.string.card_stack_list_screen_empty_state_description))
                    AppButton(
                        onClick = { navHandler.navigate(CreateNewCardStack, true) },
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(stringResource(R.string.card_stack_list_screen_empty_state_button_label))
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(cardStacks) { cardStack ->
                        Card {
                            Text(cardStack.title)
                            cardStack.cards.forEach {
                                Text(it.title)
                            }
                        }
                    }
                }
            }
        }
    }
}

class BlobCardShape : Shape {
    val path = Path().apply {
        moveTo(248.5f, 13f)
        cubicTo(190.088f, 17.6858f, 140f, 13.5f, 115.5f, 7.5f)
        cubicTo(91f, 1.5f, 58.5f, -2.5f, 37f, 3f)
        cubicTo(15.5f, 8.5f, 3f, 21f, 0.5f, 38f)
        cubicTo(-2f, 55f, 7f, 75f, 10.5f, 92.5f)
        cubicTo(14f, 110f, 13.5f, 132.898f, 10.5f, 150.5f)
        cubicTo(7.5f, 168.102f, -0.5f, 178f, 0.5f, 192.5f)
        cubicTo(1.5f, 207f, 5.5f, 218.5f, 24.5f, 232f)
        cubicTo(43.5f, 245.5f, 68f, 250.5f, 101.5f, 253f)
        cubicTo(135f, 255.5f, 184.5f, 248f, 210f, 242f)
        cubicTo(235.5f, 236f, 273f, 235.5f, 294.5f, 242.5f)
        cubicTo(316f, 249.5f, 343f, 263.5f, 357.5f, 252f)
        cubicTo(372f, 240.5f, 373f, 222f, 367f, 210.5f)
        cubicTo(361f, 199f, 353.575f, 161.5f, 352f, 125.5f)
        cubicTo(350.425f, 89.4998f, 392.161f, 48.5527f, 362f, 20.5f)
        cubicTo(349.776f, 9.13084f, 337f, 5f, 318f, 2f)
        cubicTo(299f, -1f, 277.761f, 10.6525f, 248.5f, 13f)
        close()
    }

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(path.transformToFitBounds(size, 0f))
    }
}

class BlobShape(val rotationDegrees: Float = 0f) : Shape {
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
        return Outline.Generic(path.transformToFitBounds(size, rotationDegrees))
    }
}

fun Path.transformToFitBounds(size: Size, rotationDegrees: Float): Path {
    val path = Path().apply {
        addPath(this@transformToFitBounds)
        transform(
            Matrix().apply {
                if (rotationDegrees != 0f) {
                    rotateZ(rotationDegrees)
                }
            }
        )
    }
    val pathBounds = path.getBounds()

    val scaleX = size.width / pathBounds.width
    val scaleY = size.height / pathBounds.height

    return Path().apply {
        addPath(path) // Add the original path
        transform(Matrix().apply { scale(scaleX, scaleY) })
        translate(Offset(-pathBounds.left * scaleX, -pathBounds.top * scaleY)) // Translate
    }
}

@Preview
@Composable
fun CardStackListScreenPreview() {
    AppTheme {
        CardStackListScreen(emptyList(), { _, _ -> })
    }
}