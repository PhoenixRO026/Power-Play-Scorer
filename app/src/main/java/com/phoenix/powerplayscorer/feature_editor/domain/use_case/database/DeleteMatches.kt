package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository

class DeleteMatches(
    private val repository: Repository
) {
    suspend operator fun invoke(
        deletedMatches: List<Match>
    ) {
        val onlineMatches = mutableListOf<Match>()
        val offlineMatches = mutableListOf<Match>()
        for (match in deletedMatches) {
            if (match.userId == "offline") {
                offlineMatches.add(match)
            } else {
                onlineMatches.add(match)
            }
        }
        repository.insertMatches(
            onlineMatches.map {
                it.copy(
                    toBeDeleted = true
                )
            }
        )
        repository.deleteMatches(offlineMatches)
    }
}