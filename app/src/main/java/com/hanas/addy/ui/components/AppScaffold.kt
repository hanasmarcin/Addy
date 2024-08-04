package com.hanas.addy.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hanas.addy.R
import com.hanas.addy.ui.drawPattern
import com.hanas.addy.view.home.NavigationHandler

@Composable
fun AppScaffold(
    navHandler: NavigationHandler,
    modifier: Modifier = Modifier,
    hasBackButton: Boolean = true,
    topBarTitle: (@Composable () -> Unit)?,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: @Composable() (BoxScope.() -> Unit),
) {
    Scaffold(
        topBar = {
            topBarTitle?.let {
                AppTopBar(hasBackButton, navHandler, it, actions)
            }
        },
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