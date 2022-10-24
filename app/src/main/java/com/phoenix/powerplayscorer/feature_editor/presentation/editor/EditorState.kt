package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import android.os.Parcelable
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorState(
    val match: Match,
    val isEditEnabled: Boolean,
    val isNewMatch: Boolean
): Parcelable

data class MappedEditorState(
    val match: Match = Match(),
    val isEditEnabled: Boolean = true,
    val isNewMatch: Boolean = true,
    val matchLimits: MatchLimits = MatchLimits(Match()),
    val matchTotals: MatchTotals = MatchTotals(Match()),
    val matchVisibility: MatchVisibiliy = MatchVisibiliy(Match()),
    val twoTeams: Boolean = false
)

class MatchTotals (
    match: Match
) {
    val autoTotal1 = calculateAutoPoints1(match)
    val autoTotal2 = calculateAutoPoints2(match)
    val autoTotal = calculateAutoPointsTotal(match)

    val driverTotal1 = calculateAutoPoints1(match)
    val driverTotal2 = calculateAutoPoints2(match)
    val driverTotal = calculateAutoPointsTotal(match)

    val endgameTotal1 = calculateEndgamePoints1(match)
    val endgameTotal2 = calculateEndgamePoints2(match)
    val endgameTotal = calculateEndgamePointsTotal(match)

    val total1 = calculateTotalPoints1(match)
    val total2 = calculateTotalPoints2(match)
    val total = calculateTotalPoints(match)
}

class MatchVisibiliy(
    match: Match
) {
    val customSignalSleeve1 = match.autoParked1 == true
    val customSignalSleeve2 = match.autoParked2 == true && match.twoTeams
}

class MatchLimits(
    match: Match
) {
    private val autoUpperLimit =
            12 - match.autoTerminal - match.autoGroundJunction - match.autoLowJunction -
            match.autoMediumJunction - match.autoHighJunction - match.autoTerminal2 -
            match.autoGroundJunction2 - match.autoLowJunction2 - match.autoMediumJunction2 -
            match.autoHighJunction2
    private val driverUpperLimit = driverUpperLimits(match)

    private val endgameUpperLimit =
            25 - match.junctionsOwnedByCone - match.junctionsOwnedByBeacons -
            match.junctionsOwnedByCone2 - match.junctionsOwnedByBeacons2

    private val beaconUpperLimit = 2 - match.junctionsOwnedByBeacons - match.junctionsOwnedByBeacons2

    val autoPlusEnabled = autoUpperLimit > 0
    val driverPlusEnabled = driverUpperLimit > 0
    val endgameConePlusEnabled = endgameUpperLimit > 0
    val endgameBeaconPlusEnabled = endgameConePlusEnabled && beaconUpperLimit > 0
}

fun driverUpperLimits(match: Match) = 30 - match.driverTerminal - match.driverGroundJunction - match.driverLowJunction -
        match.driverMediumJunction - match.driverHighJunction - match.driverTerminal2 -
        match.driverGroundJunction2 - match.driverLowJunction2 - match.driverMediumJunction2 -
        match.driverHighJunction2

operator fun Int.minus(other: Boolean?) = this - other.toInt()

private fun Boolean?.toInt(): Int = when (this) {
    true -> 2
    false -> 1
    else -> 0
}