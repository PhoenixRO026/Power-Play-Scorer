package com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth

import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignOut(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Response<Unit>> {
        return repository.signOut()
    }
}