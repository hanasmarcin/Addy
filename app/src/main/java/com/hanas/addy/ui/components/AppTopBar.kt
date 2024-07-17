package com.hanas.addy.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hanas.addy.home.BlobShape
import com.hanas.addy.home.GoBack
import com.hanas.addy.home.NavigationHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navHandler: NavigationHandler, topBarTitleContent: @Composable () -> Unit) {
    TopAppBar(
        title = topBarTitleContent,
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
}
