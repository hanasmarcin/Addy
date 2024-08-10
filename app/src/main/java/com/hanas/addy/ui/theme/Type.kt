package com.hanas.addy.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanas.addy.R

@OptIn(ExperimentalTextApi::class) val bodyFontFamily = FontFamily(
    Font(
        R.font.signika_variable, variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
        )
    )
)

@OptIn(ExperimentalTextApi::class) val displayFontFamily = FontFamily(
    Font(
        R.font.signika_variable, variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
        )
    )
)


// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = bodyFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = bodyFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = bodyFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)

@Composable
fun TextStyleDisplay(fontName: String, styleName: String, textStyle: TextStyle) {
    Column {
        Text(
            text = "Font: $fontName\nStyle: $styleName",
            style = textStyle,
        )
        HorizontalDivider(Modifier.fillMaxWidth())
//        Text(
//            text = "The quick brown fox jumps over the lazy dog.",
//            style = textStyle
//        )
    }
}

@Preview(widthDp = 1000, heightDp = 2000)
@Composable
fun PreviewAllTextStyles() {
    Surface(
        modifier = Modifier.padding(16.dp)
    ) {
        Column {
            TextStyleDisplay("Odibee", "DisplayLarge", AppTypography.displayLarge)
            TextStyleDisplay("Odibee", "DisplayMedium", AppTypography.displayMedium)
            TextStyleDisplay("Odibee", "DisplaySmall", AppTypography.displaySmall)
            TextStyleDisplay("Odibee", "HeadlineLarge", AppTypography.headlineLarge)
            TextStyleDisplay("Odibee", "HeadlineMedium", AppTypography.headlineMedium)
            TextStyleDisplay("Odibee", "HeadlineSmall", AppTypography.headlineSmall)
            TextStyleDisplay("Atkinson Hyperlegible", "TitleLarge", AppTypography.titleLarge)
            TextStyleDisplay("Atkinson Hyperlegible", "BodyLarge", AppTypography.bodyLarge)
            TextStyleDisplay("Atkinson Hyperlegible", "LabelLarge", AppTypography.labelLarge)
            TextStyleDisplay("Atkinson Hyperlegible", "TitleMedium", AppTypography.titleMedium)
            TextStyleDisplay("Atkinson Hyperlegible", "BodyMedium", AppTypography.bodyMedium)
            TextStyleDisplay("Atkinson Hyperlegible", "LabelSmall", AppTypography.labelSmall)
            TextStyleDisplay("Atkinson Hyperlegible", "TitleSmall", AppTypography.titleSmall)
            TextStyleDisplay("Atkinson Hyperlegible", "BodySmall", AppTypography.bodySmall)
        }
    }
}


