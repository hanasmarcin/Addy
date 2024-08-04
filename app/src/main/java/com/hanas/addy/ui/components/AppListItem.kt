package com.hanas.addy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppTheme

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    position: ListItemPosition = ListItemPosition.STANDALONE,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    color: Color = AppColors.blue,
    onClick: () -> Unit,
    content: @Composable () -> Unit,

    ) {
    ListItem(
        modifier = modifier
            .clip(position.outerShape)
            .background(color)
            .padding(position.padding)
            .clip(position.innerShape)
            .clickable(onClick = onClick),
        leadingContent = leadingContent,
        headlineContent = content,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
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

enum class ListItemPosition(
    val outerShape: Shape,
    val padding: PaddingValues,
    val innerShape: Shape
) {
    FIRST(
        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        PaddingValues(4.dp, 2.dp, 4.dp, 1.dp),
        RoundedCornerShape(6.dp, 6.dp, 2.dp, 2.dp)
    ),
    LAST(
        RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
        PaddingValues(4.dp, 1.dp, 4.dp, 8.dp),
        RoundedCornerShape(2.dp, 2.dp, 6.dp, 6.dp)
    ),
    MIDDLE(
        RectangleShape,
        PaddingValues(4.dp, 1.dp, 4.dp, 1.dp),
        RoundedCornerShape(2.dp)
    ),
    STANDALONE(
        RoundedCornerShape(8.dp),
        PaddingValues(4.dp, 2.dp, 4.dp, 8.dp),
        RoundedCornerShape(6.dp)
    )
}

inline fun <T> LazyListScope.itemsWithPosition(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(item: T, position: ListItemPosition, index: Int) -> Unit
) = itemsIndexed(items, key, contentType) { index, item ->
    val position = when {
        items.size == 1 -> ListItemPosition.STANDALONE
        index == 0 -> ListItemPosition.FIRST
        index == items.lastIndex -> ListItemPosition.LAST
        else -> ListItemPosition.MIDDLE
    }
    itemContent(item, position, index)
}