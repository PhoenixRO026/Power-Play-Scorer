package com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth

import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LoginOnline(
    private val repository: AuthRepository
) {
    operator fun invoke(
        email: String,
        password: String
    ): Flow<Response<Unit>> {
        return repository.loginOnline(email, password)
    }
}