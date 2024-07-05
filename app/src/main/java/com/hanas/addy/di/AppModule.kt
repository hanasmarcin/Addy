package com.hanas.addy.di

import com.hanas.addy.home.GeminiRepository
import com.hanas.addy.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    factoryOf(::GeminiRepository)
    viewModelOf(::HomeViewModel)
}