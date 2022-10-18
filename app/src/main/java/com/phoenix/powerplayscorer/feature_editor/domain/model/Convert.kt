package com.phoenix.powerplayscorer.feature_editor.domain.model

import com.google.firebase.Timestamp

fun FirebaseMatch.toMatch (key: String, uid: String): Match {
    return Match(
        key = key,
        userId = uid,
        uploadStamp = this.uploadStamp.toLong(),
        title = this.title,
        favorite = this.favorite,
        status = 1,
        alliance = this.alliance,
        autoGroundJunction = this.autoGroundJunction,
        autoHighJunction = this.autoHighJunction,
        autoLowJunction = this.autoLowJunction,
        autoParked1 = this.autoParked1,
        autoParked2 = this.autoParked2,
        autoMediumJunction = this.autoMediumJunction,
        autoTerminal = this.autoTerminal,
        circuitCompleted = this.circuitCompleted,
        createStamp = this.createStamp,
        customSignalSleeve1 = this.customSignalSleeve1,
        customSignalSleeve2 = this.customSignalSleeve2,
        driverGroundJunction = this.driverGroundJunction,
        driverHighJunction = this.driverHighJunction,
        driverLowJunction = this.driverLowJunction,
        driverTerminal = this.driverTerminal,
        editStamp = this.editStamp,
        endParked1 = this.endParked1,
        endParked2 = this.endParked2,
        driverMediumJunction = this.driverMediumJunction,
        junctionsOwnedByBeacons = this.junctionsOwnedByBeacons,
        totalPoints = this.totalPoints,
        twoTeams = this.twoTeams,
        junctionsOwnedByCone = this.junctionsOwnedByCone
    )
}

fun Match.toFirebaseMatch(): FirebaseMatch {
    return FirebaseMatch(
        uploadStamp = null,
        title = this.title,
        alliance = this.alliance,
        favorite = this.favorite,
        autoGroundJunction = this.autoGroundJunction,
        autoHighJunction = this.autoHighJunction,
        autoLowJunction = this.autoLowJunction,
        autoParked1 = this.autoParked1,
        autoParked2 = this.autoParked2,
        autoMediumJunction = this.autoMediumJunction,
        autoTerminal = this.autoTerminal,
        circuitCompleted = this.circuitCompleted,
        createStamp = this.createStamp,
        customSignalSleeve1 = this.customSignalSleeve1,
        customSignalSleeve2 = this.customSignalSleeve2,
        driverGroundJunction = this.driverGroundJunction,
        driverHighJunction = this.driverHighJunction,
        driverLowJunction = this.driverLowJunction,
        driverTerminal = this.driverTerminal,
        editStamp = this.editStamp,
        endParked1 = this.endParked1,
        endParked2 = this.endParked2,
        driverMediumJunction = this.driverMediumJunction,
        junctionsOwnedByBeacons = this.junctionsOwnedByBeacons,
        totalPoints = this.totalPoints,
        twoTeams = this.twoTeams,
        junctionsOwnedByCone = this.junctionsOwnedByCone
    )
}

fun Long.toTimestamp(): Timestamp {
    return Timestamp(
        this / 1000,
        ((this % 1000) * 1000000).toInt()
    )
}

fun Timestamp?.toLong(): Long? {
    if (this == null) return null
    return this.seconds * 1000 + this.nanoseconds / 1000000
}