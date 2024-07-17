package com.hanas.addy.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
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