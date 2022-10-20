package com.phoenix.powerplayscorer.feature_editor.data.data_source

import androidx.room.*
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM `match` WHERE `userId` = :userId AND NOT toBeDeleted")
    fun getMatches(userId: String): Flow<List<Match>>

    @Query(
        "SELECT * FROM `match` WHERE " +
                "`userId` = :userId " +
                "AND status = 1 " +
                "AND toBeDeleted"
    )
    fun getOnlineDeletedMatchesFlow(userId: String): Flow<List<Match>>

    @Query(
        "SELECT * FROM `match` WHERE " +
                "`userId` = :userId " +
                "AND (status = 3 OR status = 4) " +
                "AND toBeDeleted"
    )
    fun getFailedDeletedMatchesFlow(userId: String): Flow<List<Match>>

    @Query("SELECT * FROM `match` WHERE `key` = :key")
    fun getMatchByKey(key: String): Flow<Match?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matchList: List<Match>)

    @Delete
    suspend fun deleteMatch(match: Match)

    @Delete
    suspend fun deleteMatches(matches: List<Match>)

    @Query("DELETE FROM `match` WHERE `key` = :key")
    suspend fun deleteMatchByKey(key: String)

    @Query("DELETE FROM `match` WHERE `key`IN (:keyList)")
    suspend fun deleteMatchListByKeys(keyList: List<String>)

    @Query("SELECT MAX(uploadStamp) AS newestUpload FROM `match` WHERE `userId` = :userId AND status = 1 AND NOT toBeDeleted")
    suspend fun getLatestUploadStamp(userId: String): Long?

    @Query("SELECT * FROM `match` WHERE status = 0 AND NOT toBeDeleted AND `userId` = :userId")
    fun getMatchesToBeUploaded(userId: String): Flow<List<Match>>

    @Query("SELECT * FROM `match` WHERE status = 2 AND NOT toBeDeleted AND `userId` = :userId")
    fun getMatchesToBeUpdated(userId: String): Flow<List<Match>>

    @Query("SELECT `key` FROM `Match` WHERE status = 1 AND userId = :userId")
    suspend fun getUploadedMatchesKeys(userId: String): List<String>

}