package com.hanas.addy.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.work.WorkManager
import coil.ImageLoader
import com.hanas.addy.view.cardStackDetail.CardStackDetailViewModel
import com.hanas.addy.view.cardStackList.CardStackListViewModel
import com.hanas.addy.view.cardStackList.CardStackRepository
import com.hanas.addy.view.createNewCardStack.CreateNewCardStackViewModel
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.chooseGameSession.ChooseCardStackViewModel
import com.hanas.addy.view.gameSession.chooseGameSession.ChooseGameSessionViewModel
import com.hanas.addy.view.gameSession.createNewSession.CreateNewGameSessionViewModel
import com.hanas.addy.view.home.HomeViewModel
import com.hanas.addy.view.login.LoginViewModel
import com.hanas.addy.view.playTable.PlayTableViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single {
        provideImageLoader(androidContext())
    }
    single {
        provideCredentialManager(androidContext())
    }
    single {
        provideWorkManager(androidContext())
    }
    singleOf(::CardStackRepository)
    factoryOf(::GameSessionRepository)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateNewCardStackViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::CardStackListViewModel)
    viewModelOf(::CardStackDetailViewModel)
    viewModelOf(::PlayTableViewModel)
    viewModelOf(::CreateNewGameSessionViewModel)
    viewModelOf(::ChooseGameSessionViewModel)
    viewModelOf(::ChooseCardStackViewModel)
}

fun provideImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context).build()
}

fun provideCredentialManager(context: Context): CredentialManager {
    return CredentialManager.create(context)
}

fun provideWorkManager(context: Context): WorkManager {
    return WorkManager.getInstance(context)
}



