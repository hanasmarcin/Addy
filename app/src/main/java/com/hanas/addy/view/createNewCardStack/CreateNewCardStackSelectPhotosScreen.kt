package com.hanas.addy.view.createNewCardStack

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.hanas.addy.R
import com.hanas.addy.model.DataHolder
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.shapes.BlobShape
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.gameSession.chooseGameSession.observeNavigation
import org.koin.androidx.compose.navigation.koinNavViewModel

fun NavGraphBuilder.createNewCardStackSelectPhotosComposable(navController: NavController, navigateBack: () -> Unit) {
    composable<CreateNewCardStack.SelectPhotos> {
        val parent = remember(it) { navController.getBackStackEntry<CreateNewCardStack>() }
        val viewModel: CreateNewCardStackViewModel = koinNavViewModel(viewModelStoreOwner = parent)
        viewModel.observeNavigation<CreateNewCardStack.SelectPhotos>(navController)
        val photoDrawables by viewModel.photoUrisFlow.collectAsState()
        val cardStackFlow by viewModel.cardStackFlow.collectAsState()
        val cameraHelper = rememberCameraHelper {
            viewModel.addPhoto(it)
        }
        val photoPickerHelper = rememberPhotoPickerHelper {
            viewModel.addAllPhotos(it)
        }
        CreateNewCardStackSelectPhotosScreen(
            photoDrawables = photoDrawables,
            isLoading = cardStackFlow is DataHolder.Loading,
            navigateBack = navigateBack,
            pickImages = photoPickerHelper::pickPhotos,
            takePhoto = cameraHelper::takePhoto,
            generateStack = viewModel::generateStack
        )
    }
}


@Composable
private fun CreateNewCardStackSelectPhotosScreen(
    photoDrawables: List<Uri>,
    isLoading: Boolean,
    navigateBack: () -> Unit,
    pickImages: () -> Unit,
    takePhoto: () -> Unit,
    generateStack: () -> Unit
) {
    val pagerState = rememberPagerState { photoDrawables.size + 1 }
    LaunchedEffect(photoDrawables.size) {
        pagerState.scrollToPage(photoDrawables.lastIndex + 1)
        pagerState.animateScrollToPage(photoDrawables.lastIndex, animationSpec = tween(delayMillis = 100))
    }
    AppScaffold(
        navigateBack = navigateBack,
        modifier = Modifier.fillMaxSize(),
        topBarTitle = {
            Text("Add New Card Stack")
        },
        bottomBar = {
            AnimatedVisibility(photoDrawables.isNotEmpty()) {
                GenerateStackCard(isLoading, generateStack)
            }
        },
        actions = {},
        content = {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                contentPadding = PaddingValues(top = 0.dp, start = 32.dp, end = 32.dp),
                pageSpacing = 16.dp
            ) { page ->
                if (page == photoDrawables.lastIndex + 1) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)) {
                        Image(painterResource(R.drawable.boy_camera), null,
                            Modifier
                                .padding(vertical = 16.dp)
                                .weight(1f)
                                .fillMaxSize())
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.containerFor(AppColors.pink))
                                .padding(16.dp),
                        ) {
                            PrimaryButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = pickImages,
                                isLoading = isLoading,
                            ) {
                                Text("Choose from gallery")
                            }
                            Spacer(Modifier.size(8.dp))
                            PrimaryButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = takePhoto,
                                isLoading = isLoading,
                            ) {
                                Text("Take a photo")
                            }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize()) {
                        val photoUri = photoDrawables[page]
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
    )
}

@Composable
private fun GenerateStackCard(isLoading: Boolean, generateStack: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.containerFor(AppColors.yellow))
                .padding(16.dp),
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = generateStack,
                isLoading = isLoading,
                color = AppColors.yellow,
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
        GenerateStackCard(false) {}
    }
}

@Preview
@Composable
fun AddNewCardStackScreenPreview() {
    AppTheme {
//        val list = listOf(
////            "https://picsum.photos/200/800".toUri(),
////            "https://picsum.photos/200/800".toUri(),
//        )
        CreateNewCardStackSelectPhotosScreen(emptyList(), false, {}, {}, {}, {})
    }
}