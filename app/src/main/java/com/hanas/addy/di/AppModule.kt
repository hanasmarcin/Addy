package com.hanas.addy.di

import android.content.Context
import androidx.credentials.CredentialManager
import coil.ImageLoader
import com.hanas.addy.home.CreateNewCardStackViewModel
import com.hanas.addy.home.GeminiRepository
import com.hanas.addy.home.HomeViewModel
import com.hanas.addy.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    single {
        provideImageLoader(androidContext())
    }
    single {
        provideCredentialManager(androidContext())
    }
    factoryOf(::GeminiRepository)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateNewCardStackViewModel)
    viewModelOf(::LoginViewModel)
}

fun provideImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context).build()
}

fun provideCredentialManager(context: Context): CredentialManager {
    return CredentialManager.create(context)
}