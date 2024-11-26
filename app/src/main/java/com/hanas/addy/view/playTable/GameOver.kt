package com.hanas.addy.view.playTable

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.hanas.addy.R
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppListItem
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.itemsWithPosition
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.cardStackList.LabelPill
import com.hanas.addy.view.gameSession.createNewSession.Medal
import com.hanas.addy.view.gameSession.createNewSession.PlayerResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Serializable
data class GameOver(
    val results: List<PlayerResult>
) : NavScreen

val PlayerResultsType = object : NavType<List<PlayerResult>>(isNullableAllowed = false) {

    override fun serializeAsValue(value: List<PlayerResult>): String {
        return Json.encodeToString(value)
    }

    override fun put(bundle: Bundle, key: String, value: List<PlayerResult>) {
        val jsonValue = Json.encodeToString(value)
        bundle.putString(key, jsonValue)
    }

    override fun get(bundle: Bundle, key: String): List<PlayerResult>? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): List<PlayerResult> {
        return Json.decodeFromString(value)
    }
}

fun NavGraphBuilder.gameOverComposable(navigateBack: () -> Unit) {
    composable<GameOver>(
        typeMap = mapOf(typeOf<List<PlayerResult>>() to PlayerResultsType)
    ) {
        val finalResult = it.toRoute<GameOver>().results
        GameOverScreen(finalResult.toList(), navigateBack)
    }
}

@Composable
private fun GameOverScreen(finalResult: List<PlayerResult>, navigateBack: () -> Unit) {
    AppScaffold(
        navigateBack = navigateBack,
        topBarTitle = { Text(text = "Game over!") },
        bottomBar = {
            PrimaryButton(
                navigateBack,
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Image(
                painterResource(R.drawable.podium), null,
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
            Spacer(Modifier.size(8.dp))
            LazyColumn(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerFor(blue))
                    .padding(16.dp)
            ) {
                itemsWithPosition(finalResult) { item, position, index ->
                    val medal = when (item.medal) {
                        Medal.GOLD -> "\uD83E\uDD47"
                        Medal.SILVER -> "\uD83E\uDD48"
                        Medal.BRONZE -> "\uD83E\uDD49"
                        Medal.NONE -> null
                    }
                    AppListItem(
                        position = position,
                        isLoading = false,
                        enabled = false,
                        onClick = {},
                        trailingContent = {
                            LabelPill(stringResource(R.string.points, item.points))
                        },
                        leadingContent = medal?.let {
                            @Composable {
                                Text(
                                    medal,
                                    Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(containerFor(blue))
                                        .padding(vertical = 2.dp),
                                    style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = (-6).sp)
                                )
                            }
                        }
                    ) {
                        Text(text = item.playerDisplayName)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GameOverScreenPreview() {
    AppTheme {
        GameOverScreen(
            listOf(
                PlayerResult(Medal.GOLD, "Marcin", 50),
                PlayerResult(Medal.SILVER, "Stefan", 30),
                PlayerResult(Medal.BRONZE, "Janusz", 20)
            )
        ) { }
    }
}
