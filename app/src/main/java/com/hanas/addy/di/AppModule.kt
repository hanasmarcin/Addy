package com.hanas.addy.di

import android.content.Context
import androidx.credentials.CredentialManager
import coil.ImageLoader
import com.hanas.addy.repository.GeminiRepository
import com.hanas.addy.view.friendList.FriendListViewModel
import com.hanas.addy.view.cardStackDetail.CardStackDetailViewModel
import com.hanas.addy.view.cardStackList.CardStackListViewModel
import com.hanas.addy.view.cardStackList.PlayCardRepository
import com.hanas.addy.view.createNewCardStack.CreateNewCardStackViewModel
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
    factoryOf(::GeminiRepository)
    singleOf(::PlayCardRepository)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateNewCardStackViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::CardStackListViewModel)
    viewModelOf(::CardStackDetailViewModel)
    viewModelOf(::PlayTableViewModel)
    viewModelOf(::FriendListViewModel)
}

fun provideImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context).build()
}

fun provideCredentialManager(context: Context): CredentialManager {
    return CredentialManager.create(context)
}

