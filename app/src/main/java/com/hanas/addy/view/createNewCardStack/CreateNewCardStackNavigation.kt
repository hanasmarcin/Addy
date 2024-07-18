package com.hanas.addy.view.createNewCardStack

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable

@Serializable
object CreateNewCardStack : NavScreen {
    @Serializable
    object SelectPhotos : NavScreen

    @Serializable
    object PreviewNewStack : NavScreen

}


fun NavGraphBuilder.createNewCardStackNavigation(navHandler: NavigationHandler, navController: NavController) {
    navigation<CreateNewCardStack>(CreateNewCardStack.SelectPhotos) {
        createNewCardStackSelectPhotosComposable(navHandler, navController)
        viewNewCardStackComposable(navHandler, navController)
    }
}