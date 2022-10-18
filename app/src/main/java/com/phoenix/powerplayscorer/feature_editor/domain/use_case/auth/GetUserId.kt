package com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth

import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository

class GetUserId(
    private val repository: AuthRepository
) {
    operator fun invoke(): String {
        return repository.getUserId()
    }
}