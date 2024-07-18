package com.hanas.addy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.hanas.addy.R
import com.hanas.addy.home.NavigationHandler
import com.hanas.addy.home.drawPattern

@Composable
fun AppScaffold(
    navHandler: NavigationHandler,
    modifier: Modifier = Modifier,
    hasBackButton: Boolean = true,
    topBarTitle: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: @Composable() (BoxScope.() -> Unit),
) {
    val color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f).compositeOver(MaterialTheme.colorScheme.background)
    Scaffold(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .drawPattern(R.drawable.graph_paper, tint = color),
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(hasBackButton, navHandler, topBarTitle, actions)
        },
        bottomBar = bottomBar
    ) {
        Box(
            Modifier.fillMaxSize().padding(it)
        ) {
            content()
        }
    }
}