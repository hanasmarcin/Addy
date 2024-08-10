package com.hanas.addy.view.createNewCardStack

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.hanas.addy.ui.NavScreen
import kotlinx.serialization.Serializable

@Serializable
object CreateNewCardStack : NavScreen {
    @Serializable
    object SelectPhotos : NavScreen

    @Serializable
    object PreviewNewStack : NavScreen

}


fun NavGraphBuilder.createNewCardStackNavigation(navController: NavController, navigateBack: () -> Unit) {
    navigation<CreateNewCardStack>(CreateNewCardStack.SelectPhotos) {
        createNewCardStackSelectPhotosComposable(navController, navigateBack)
        viewNewCardStackComposable(navController, navigateBack)
    }
}