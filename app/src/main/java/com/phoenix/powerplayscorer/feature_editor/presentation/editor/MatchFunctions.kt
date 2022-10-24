package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import com.phoenix.powerplayscorer.feature_editor.domain.model.Match

fun calculateAutoPoints1(it: Match): Int {
    return  it.autoTerminal +
            it.autoGroundJunction * 2 +
            it.autoLowJunction * 3 +
            it.autoMediumJunction * 4 +
            it.autoHighJunction * 5 +
            when (it.autoParked1) {
                false -> 2
                true -> 10 * (it.customSignalSleeve1 + 1)
                else -> 0
            }
}

fun calculateAutoPoints2(it: Match): Int {
    return  it.autoTerminal2 +
            it.autoGroundJunction2 * 2 +
            it.autoLowJunction2 * 3 +
            it.autoMediumJunction2 * 4 +
            it.autoHighJunction2 * 5 +
            when (it.autoParked2) {
                false -> 2
                true -> 10 * (it.customSignalSleeve2 + 1)
                else -> 0
            }
}

fun calculateAutoPointsTotal(it: Match) = calculateAutoPoints1(it) + calculateAutoPoints2(it) * it.twoTeams

fun calculateDriverPoints1(it: Match): Int {
    return  it.driverTerminal +
            it.driverGroundJunction * 2 +
            it.driverLowJunction * 3 +
            it.driverMediumJunction * 4 +
            it.driverHighJunction * 5
}

fun calculateDriverPoints2(it: Match): Int {
    return  it.driverTerminal2 +
            it.driverGroundJunction2 * 2 +
            it.driverLowJunction2 * 3 +
            it.driverMediumJunction2 * 4 +
            it.driverHighJunction2 * 5
}

fun calculateDriverPointsTotal(it: Match) = calculateDriverPoints1(it) + calculateDriverPoints2(it) * it.twoTeams

fun calculateEndgamePoints1(it: Match): Int {
    return  it.junctionsOwnedByCone * 3 +
            it.junctionsOwnedByBeacons * 10 +
            it.endParked1 * 2
}

fun calculateEndgamePoints2(it: Match): Int {
    return  it.junctionsOwnedByCone2 * 3 +
            it.junctionsOwnedByBeacons2 * 10 +
            it.endParked2 * 2
}

fun calculateEndgamePointsTotal(it: Match) = calculateEndgamePoints1(it) + calculateEndgamePoints2(it) * it.twoTeams

fun calculateTotalPoints1(it: Match): Int {
    val autoPoints = calculateAutoPoints1(it)
    val driverPoints = calculateDriverPoints1(it)
    val endgamePoints = calculateEndgamePoints1(it)
    return autoPoints + driverPoints + endgamePoints
}

fun calculateTotalPoints2(it: Match): Int {
    val autoPoints = calculateAutoPoints2(it)
    val driverPoints = calculateDriverPoints2(it)
    val endgamePoints = calculateEndgamePoints2(it)
    return autoPoints + driverPoints + endgamePoints
}

fun calculateTotalPoints(it: Match): Int {
    val autoPoints = calculateAutoPointsTotal(it)
    val driverPoints = calculateDriverPointsTotal(it)
    val endgamePoints = calculateEndgamePointsTotal(it)
    return autoPoints + driverPoints + endgamePoints
}