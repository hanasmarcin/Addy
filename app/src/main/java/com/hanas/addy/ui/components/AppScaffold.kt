package com.hanas.addy.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hanas.addy.R
import com.hanas.addy.ui.drawPattern

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    navigateBack: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topBarTitle: (@Composable () -> Unit)?,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: @Composable() (BoxScope.() -> Unit),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            topBarTitle?.let {
                AppTopBar(navigateBack, it, actions)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Box(
            Modifier
                .drawPattern(R.drawable.graph_paper, tint = Color.Black.copy(alpha = 0.05f))
                .fillMaxSize()
                .padding(it)
        ) {
            content()
        }
    }
}