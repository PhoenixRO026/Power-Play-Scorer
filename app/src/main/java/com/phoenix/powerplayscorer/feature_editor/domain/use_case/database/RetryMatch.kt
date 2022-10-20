package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository

class RetryMatch(
    private val repository: Repository
) {
    suspend operator fun invoke(
        match: Match
    ) {
        when (match.status) {
            3 -> {
                repository.insertMatch(
                    match.copy(
                        status = 0
                    )
                )
            }
        }
    }
}