package com.phoenix.powerplayscorer.feature_editor.domain.repository

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getMatches(): Flow<List<Match>>

    fun getMatchByKey(key: String): Flow<Match?>

    suspend fun insertMatches(matchList: List<Match>)

    suspend fun insertMatch(match: Match)

    suspend fun deleteMatches(matchList: List<Match>)
}