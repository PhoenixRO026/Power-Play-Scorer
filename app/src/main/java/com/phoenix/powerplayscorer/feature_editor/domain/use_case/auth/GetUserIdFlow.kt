package com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth

import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class GetUserIdFlow(
    private val repository: AuthRepository
) {
    operator fun invoke(
    ): StateFlow<String?> {
        return repository.getUidFlow()
    }
}