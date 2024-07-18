package com.hanas.addy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.hanas.addy.home.drawPattern

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    position: ListItemPosition = ListItemPosition.STANDALONE,
    leadingContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    pattern: Int,
    onClick: () -> Unit,
    content: @Composable () -> Unit,

    ) {
    Surface(
        modifier = modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth(),
        color = color,
        onClick = onClick,
        shape = position.shape
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .drawPattern(pattern, tint = Color.White)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            leadingContent()
            Box(Modifier.weight(1f)) {
                content()
            }
            trailingContent()
        }
    }
}

enum class ListItemPosition(val shape: Shape) {
    FIRST(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    LAST(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
    MIDDLE(RectangleShape),
    STANDALONE(RoundedCornerShape(16.dp))
}

inline fun <T> LazyListScope.itemsWithPosition(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(item: T, position: ListItemPosition, index: Int) -> Unit
) = itemsIndexed(items, key, contentType) { index, item ->
    val position = when {
        index == 0 -> ListItemPosition.FIRST
        index == items.lastIndex -> ListItemPosition.LAST
        items.size == 1 -> ListItemPosition.STANDALONE
        else -> ListItemPosition.MIDDLE
    }
    itemContent(item, position, index)
}