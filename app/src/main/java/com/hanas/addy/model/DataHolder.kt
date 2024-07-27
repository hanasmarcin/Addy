package com.hanas.addy.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class DataHolder<T>(
    open val data: T?,
    open val error: Throwable?
) {
    class Idle<T> : DataHolder<T>(null, null)

    data class Loading<T>(
        val cachedData: T? = null,
        val cachedError: Throwable? = null
    ) : DataHolder<T>(cachedData, cachedError)

    data class Success<T>(
        override val data: T
    ) : DataHolder<T>(data, null)

    data class Error<T>(
        override val error: Throwable,
        val cachedData: T? = null
    ) : DataHolder<T>(cachedData, error)
}

fun <T> Flow<T>.wrapInDataHolder(): Flow<DataHolder<T>> = this
    .map<T, DataHolder<T>> { DataHolder.Success(it) }
    .onStart { emit(DataHolder.Loading()) }
    .catch { emit(DataHolder.Error(it)) }
