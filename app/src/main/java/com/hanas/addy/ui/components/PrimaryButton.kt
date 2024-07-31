package com.hanas.addy.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.ui.theme.AppTheme

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        enabled = isLoading.not(),
        modifier = modifier,
        onClick = onClick
    ) {
        AnimatedContent(isLoading, label = "") { loading ->
            if (loading) {
                with(LocalDensity.current) {
                    CircularProgressIndicator(
                        Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp()),
                        strokeWidth = 3.dp,
                        //color = MaterialTheme.colorScheme.onPrimary
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

@Preview
@Composable
fun AppButtonPreview() {
    AppTheme {
        Surface {
            Row {
                PrimaryButton(modifier = Modifier.padding(32.dp), onClick = {}) {
                    Text("Button ggrsfehtrs")
                }
                PrimaryButton(modifier = Modifier.padding(32.dp), isLoading = true, onClick = {}) {
                    Text("Button ggrsfehtrs")
                }
            }
        }
    }
}


