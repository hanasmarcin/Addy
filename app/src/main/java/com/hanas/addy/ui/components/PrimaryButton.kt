package com.hanas.addy.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.ui.theme.AppTheme

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(contentColor)
            .padding(3.dp, 1.dp, 3.dp, 8.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(surfaceColor)
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides MaterialTheme.typography.labelLarge
        ) {
            AnimatedContent(isLoading, label = "") { loading ->
                if (loading) {
                    with(LocalDensity.current) {
                        CircularProgressIndicator(
                            Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp()),
                            color = contentColor,
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, content = content
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AppButtonPreview() {
    AppTheme {
        Surface {
            Column {
                Row {
                    PrimaryButton(modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(32.dp), onClick = {}) {
                        Text("Button ggrsfehtrs")
                    }
                    PrimaryButton(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(32.dp),
                        isLoading = true,
                        contentColor = MaterialTheme.colorScheme.surface,
                        surfaceColor = MaterialTheme.colorScheme.onSurface,
                        onClick = {}) {
                        Text("Button ggrsfehtrs")
                    }
                }
                Column {
                    Row {
                        PrimaryButton(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(32.dp),
                            contentColor = MaterialTheme.colorScheme.surface,
                            surfaceColor = MaterialTheme.colorScheme.onSurface,
                            onClick = {}) {
                            Text("Button ggrsfehtrs")
                        }
                        PrimaryButton(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(32.dp),
                            isLoading = true,
                            onClick = {}) {
                            Text("Button ggrsfehtrs")
                        }
                    }
                }
            }
        }
    }
}


