package com.phoenix.powerplayscorer.feature_editor.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phoenix.powerplayscorer.feature_editor.domain.util.autoId
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@Entity
data class Match(
    @PrimaryKey val key: String = autoId(),

    val userId: String = "offline",
    val title: String = "",
    val createStamp: Long = 0,
    val editStamp: Long = 0,
    val uploadStamp: Long? = null,
    val totalPoints: Int = 0,
    val twoTeams: Boolean = false,
    val alliance: Boolean? = null,
    val favorite: Boolean = false,

    /**
     * 0 = to be uploaded
     * 1 = uploaded
     * 2 = to be updated
     */
    val status :Int = 0,
    val toBeDeleted: Boolean = false,

    val autoTerminal: Int = 0,
    val autoGroundJunction: Int = 0,
    val autoLowJunction: Int = 0,
    val autoMediumJunction: Int = 0,
    val autoHighJunction: Int = 0,
    val autoParked1: Boolean? = null,
    val customSignalSleeve1: Boolean = false,
    val autoParked2: Boolean? = null,
    val customSignalSleeve2: Boolean = false,

    val driverTerminal: Int = 0,
    val driverGroundJunction: Int = 0,
    val driverLowJunction: Int = 0,
    val driverMediumJunction: Int = 0,
    val driverHighJunction: Int = 0,

    val junctionsOwnedByCone: Int = 0,
    val junctionsOwnedByBeacons: Boolean? = null,
    val circuitCompleted: Boolean = false,
    val endParked1: Boolean = false,
    val endParked2: Boolean = false
) : Parcelable
