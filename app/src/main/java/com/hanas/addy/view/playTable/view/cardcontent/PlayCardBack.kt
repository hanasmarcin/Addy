package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hanas.addy.R

@Composable
fun CardBack(modifier: Modifier = Modifier) {
    Box(
        modifier
            .rotate(180f)
            .aspectRatio(CARD_ASPECT_RATIO)
            .fillMaxSize()
            .paperBackground(rememberPaperBrush(), MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painterResource(R.drawable.book_pattern_bauhaus_imagen),
            null,
            contentScale = ContentScale.Inside,
            modifier = Modifier.fillMaxSize()
        )
    }
}

