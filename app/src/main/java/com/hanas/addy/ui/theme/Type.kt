@file:OptIn(ExperimentalTextApi::class)

package com.hanas.addy.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.hanas.addy.R

val AppFontFamily = FontFamily(
    Font(
        R.font.ojuju_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Normal.weight),
        )
    ),
    Font(
        R.font.ojuju_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Medium.weight),
        )
    ),
    Font(
        R.font.ojuju_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.SemiBold.weight),
        )
    ),
    Font(
        R.font.ojuju_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Bold.weight),
        )
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 60.sp,
        lineHeight = 68.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 46.sp,
        lineHeight = 54.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 38.sp,
        lineHeight = 46.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 27.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 27.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    /* Other default text styles to override
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

@Preview(heightDp = 1200, widthDp = 800)
@Composable
fun GreetingPreview() {
    AddyTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier) {
                val typography = remember { Typography() }
                val newTypography = Typography
                Row {
                    Text("Display large\nnew line", style = typography.displayLarge)
                    Text("Display large\nnew line", style = newTypography.displayLarge)
                }
                HorizontalDivider()
                Row {
                    Text("Display medium\nnew line", style = typography.displayMedium)
                    Text("Display medium\nnew line", style = newTypography.displayMedium)
                }
                HorizontalDivider()
                Row {
                    Text("Display small\nnew line", style = typography.displaySmall)
                    Text("Display small\nnew line", style = newTypography.displaySmall)
                }
                HorizontalDivider()
                Row {
                    Text("Headline large\nnew line", style = typography.headlineLarge)
                    Text("Headline large\nnew line", style = newTypography.headlineLarge)
                }
                HorizontalDivider()
                Row {
                    Text("Headline medium\nnew line", style = typography.headlineMedium)
                    Text("Headline medium\nnew line", style = newTypography.headlineMedium)

                }
                HorizontalDivider()
                Row {
                    Text("Headline small\nnew line", style = typography.headlineSmall)
                    Text("Headline small\nnew line", style = newTypography.headlineSmall)
                }
                HorizontalDivider()
                Row {
                    Text("Title large\nnew line", style = typography.titleLarge)
                    Text("Title large\nnew line", style = newTypography.titleLarge)
                }
                HorizontalDivider()
                Row {
                    Text("Title medium\nnew line", style = typography.titleMedium)
                    Text("Title medium\nnew line", style = newTypography.titleMedium)
                }
                HorizontalDivider()
                Row {
                    Text("Title small\nnew line", style = typography.titleSmall)
                    Text("Title small\nnew line", style = newTypography.titleSmall)
                }
                HorizontalDivider()
                Row {
                    Text("Body large\nnew line", style = typography.bodyLarge)
                    Text("Body large\nnew line", style = newTypography.bodyLarge)
                }
                HorizontalDivider()
                Row {
                    Text("Body medium\nnew line", style = typography.bodyMedium)
                    Text("Body medium\nnew line", style = newTypography.bodyMedium)
                }
                HorizontalDivider()
                Row {
                    Text("Body small\nnew line", style = typography.bodySmall)
                    Text("Body small\nnew line", style = newTypography.bodySmall)
                }
                HorizontalDivider()
                Row {
                    Text("Label large\nnew line", style = typography.labelLarge)
                    Text("Label large\nnew line", style = newTypography.labelLarge)
                }
                HorizontalDivider()
                Row {
                    Text("Label medium\nnew line", style = typography.labelMedium)
                    Text("Label medium\nnew line", style = newTypography.labelMedium)
                }
                HorizontalDivider()
                Row {
                    Text("Label small\nnew line", style = typography.labelSmall)
                    Text("Label small\nnew line", style = newTypography.labelSmall)
                }
                HorizontalDivider()
            }
        }
    }
}