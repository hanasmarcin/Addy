@file:OptIn(ExperimentalMaterial3Api::class)

package com.hanas.addy.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import org.koin.androidx.compose.navigation.koinNavViewModel

const val HOME_ROUTE = "Home"

fun NavGraphBuilder.homeComposable() {
    composable(HOME_ROUTE) {
        val viewModel: HomeViewModel = koinNavViewModel()
        HomeScreen()
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {

        }
    }
}