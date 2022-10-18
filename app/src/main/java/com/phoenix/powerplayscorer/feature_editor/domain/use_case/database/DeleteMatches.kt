package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository

class DeleteMatches(
    private val repository: Repository
) {
    suspend operator fun invoke(
        deletedMatches: List<Match>
    ) {
        repository.deleteMatches(deletedMatches)
    }
}