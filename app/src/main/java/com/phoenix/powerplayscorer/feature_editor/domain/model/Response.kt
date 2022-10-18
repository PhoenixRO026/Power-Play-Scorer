package com.phoenix.powerplayscorer.feature_editor.domain.model

sealed class Response<out T> {
    object Loading: Response<Nothing>()

    data class Success<out T>(
        val data: T
    ): Response<T>()

    data class Failure(
        val message: String?
    ): Response<Nothing>()
}