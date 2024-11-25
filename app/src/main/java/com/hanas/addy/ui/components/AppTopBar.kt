package com.hanas.addy.ui.components

import androidx.compose.foundation.layout.RowScope
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
import com.hanas.addy.ui.components.shapes.BlobShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navigateBack: (() -> Unit)?,
    topBarTitleContent: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        title = topBarTitleContent,
        actions = actions,
        navigationIcon = {
            if (navigateBack != null) {
                FilledIconButton(
                    shape = BlobShape(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    onClick = navigateBack,
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent)
    )
}
