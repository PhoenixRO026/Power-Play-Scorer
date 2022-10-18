package com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth

import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository

class IsUserSignedIn(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return repository.isUserSignedIn()
    }
}