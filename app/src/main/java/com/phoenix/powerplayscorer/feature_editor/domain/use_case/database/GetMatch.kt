package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetMatch(
    private val repository: Repository
) {
    operator fun invoke(
        key: String
    ): Flow<Match?> {
        return repository.getMatchByKey(key)
    }
}