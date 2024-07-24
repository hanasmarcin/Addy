package com.hanas.addy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.ui.theme.AppTheme

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    position: ListItemPosition = ListItemPosition.STANDALONE,
    leadingContent: @Composable () -> Unit = {},
    trailingContent: @Composable () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    onClick: () -> Unit,
    content: @Composable () -> Unit,

    ) {
    val modifier = 
    Box(
        modifier = modifier
            .heightIn(min = 56.dp)
            .clip(shape = position.shape.)
            .fillMaxWidth(),
    ) {
        Row(
            Modifier
                .fillMaxSize()
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

@Preview
@Composable
fun AppListItemPreview() {
    AppTheme {
        Surface {
            LazyColumn(contentPadding = PaddingValues(32.dp)) {
                itemsWithPosition(listOf(1, 2, 3, 4, 5)) { index, position, item ->
                    AppListItem(
                        position = position,
                        onClick = {}
                    ) {
                        Text("Item $item")
                    }
                }
            }
        }
    }
}

enum class ListItemPosition(val shape: Shape) {
    FIRST(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
    LAST(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
    MIDDLE(RectangleShape),
    STANDALONE(RoundedCornerShape(8.dp))
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