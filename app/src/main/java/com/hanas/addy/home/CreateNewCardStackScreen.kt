@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy.home

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.hanas.addy.R
import com.hanas.addy.ui.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object CreateNewCardStack : NavScreen

fun NavGraphBuilder.createNewCardStackComposable(navHandler: NavigationHandler) {
    composable<CreateNewCardStack> {
        val viewModel: CreateNewCardStackViewModel = koinNavViewModel()
        val photoUris by viewModel.photoUrisFlow.collectAsState()
        val cameraHelper = rememberCameraHelper { viewModel.addPhoto(it) }
        val photoPickerHelper = rememberPhotoPickerHelper { viewModel.addAllPhotos(it) }
        CreateNewCardStackScreen(
            navHandler = navHandler,
            photoUris = photoUris,
            pickImages = photoPickerHelper::pickPhotos,
            takePhoto = cameraHelper::takePhoto,
            generateStack = viewModel::generateStack
        )
    }
}

@Composable
private fun CreateNewCardStackScreen(
    navHandler: NavigationHandler,
    photoUris: List<Uri>,
    pickImages: () -> Unit,
    takePhoto: () -> Unit,
    generateStack: () -> Unit
) {
    val color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f).compositeOver(MaterialTheme.colorScheme.background)
    val pagerState = rememberPagerState { photoUris.size + 1 }
    LaunchedEffect(photoUris.size) {
        pagerState.scrollToPage(photoUris.lastIndex + 1)
        pagerState.animateScrollToPage(photoUris.lastIndex, animationSpec = tween(delayMillis = 100))
    }
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .drawPattern(R.drawable.graph_paper, tint = color),
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = { Text("Create new card stack") },
                navigationIcon = {
                    FilledIconButton(
                        shape = BlobShape(),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        onClick = { navHandler.navigate(GoBack) },
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            AnimatedVisibility(photoUris.isNotEmpty()) {
                GenerateStackCard(generateStack)
            }
        }
    ) {
        Box(
            Modifier.padding(it)
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                contentPadding = PaddingValues(top = 0.dp, start = 32.dp, end = 32.dp),
                pageSpacing = 16.dp
            ) { page ->
                if (page == photoUris.lastIndex + 1) {
                    Column {
                        Image(painterResource(R.drawable.girl_photographing_book), null)
                        Spacer(Modifier.size(16.dp))
                        Card(
                            Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Column(
                                Modifier
                                    .drawPattern(R.drawable.hideout, MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f))
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            ) {
                                Button(
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    onClick = pickImages,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                ) {
                                    Text("Choose from gallery")
                                }
                                Spacer(Modifier.size(8.dp))
                                Button(
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    onClick = takePhoto,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Text("Take a new photo")
                                }
                            }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize()) {
                        val photoUri = photoUris[page]
                        Box(Modifier.align(Alignment.Center)) {
                            AsyncImage(
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                model = photoUri,
                                contentDescription = null
                            )
                            FilledIconButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(8.dp, (-8).dp),
                                shape = BlobShape(),
                                onClick = {},
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            ) {
                                Icon(Icons.Default.Clear, "")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateStackCard(generateStack: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            Modifier
                .drawPattern(R.drawable.hideout, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                onClick = generateStack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            ) {
                Icon(painter = painterResource(R.drawable.auto_fix), null)
                Spacer(Modifier.size(8.dp))
                Text("Generate a stack")
            }
        }
    }
}

@Preview
@Composable
fun GenerateStackCardPreview() {
    AppTheme {
        GenerateStackCard({})
    }
}

fun Modifier.drawInvertedColors(color: Color) = drawBehind {
    drawRect(Color.White, blendMode = BlendMode.Difference)
    drawRect(color, blendMode = BlendMode.Plus)
    drawRect(color, blendMode = BlendMode.Color)
}

@Preview
@Composable
fun AddNewCardStackScreenPreview() {
    AppTheme {
        val list = listOf<Uri>(
//            "https://picsum.photos/200/800".toUri(),
//            "https://picsum.photos/200/800".toUri(),
        )
        CreateNewCardStackScreen({}, emptyList(), {}, {}, {})
    }
}
