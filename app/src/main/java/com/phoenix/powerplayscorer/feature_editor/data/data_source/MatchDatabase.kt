package com.phoenix.powerplayscorer.feature_editor.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match

@Database(
    entities = [Match::class],
    version = 1
)
abstract class MatchDatabase: RoomDatabase() {

    abstract val matchDao: MatchDao

    companion object {
        const val DATABASE_NAME = "match_db"
    }
}