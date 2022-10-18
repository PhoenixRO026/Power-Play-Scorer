package com.phoenix.powerplayscorer.feature_editor.domain.use_case.database

data class MatchUseCases(
    val getMatch: GetMatch,
    val saveMatch: SaveMatch,
    val getMatches: GetMatches,
    val saveMatches: SaveMatches,
    val deleteMatches: DeleteMatches
)
