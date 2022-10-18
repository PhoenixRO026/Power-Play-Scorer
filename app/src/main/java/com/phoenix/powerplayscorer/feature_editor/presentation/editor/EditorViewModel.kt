package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.database.MatchUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val matchUseCases: MatchUseCases,
    private val authUseCases: AuthUseCases
): ViewModel() {
    private var currentKey: String? = savedStateHandle.get<String>("key")

    val state = MutableStateFlow(
        savedStateHandle.get<Match>("state") ?: Match()
    )
    val isEditEnabled = MutableStateFlow(
        savedStateHandle.get<Boolean>("isEditEnabled") ?: (currentKey == null)
    )
    val isNewMatch = MutableStateFlow(
        savedStateHandle.get<Boolean>("isNewMatch") ?: (currentKey == null)
    )

    private var job: Job? = null

    init {
        updateSaveStateHandle()
        getMatch(currentKey)
    }

    private fun updateSaveStateHandle() {
        state.onEach {
            savedStateHandle["state"] = it
        }.launchIn(viewModelScope)
        isEditEnabled.onEach {
            savedStateHandle["isEditEnabled"] = it
        }.launchIn(viewModelScope)
        isNewMatch.onEach {
            savedStateHandle["isNewMatch"] = it
        }.launchIn(viewModelScope)
    }

    fun save() {
        isEditEnabled.update { false }
        viewModelScope.launch {
            state.value.let { match ->
                matchUseCases.saveMatch(
                    /*if (isNewMatch.value) {
                        match.copy(
                            userId = authUseCases.getUserId(),
                            createStamp = System.currentTimeMillis(),
                            editStamp = System.currentTimeMillis(),
                            totalPoints = calculateTotalPoints(match),
                            uploadStamp = null
                        )
                    } else {
                        match.copy(
                            editStamp = System.currentTimeMillis(),
                            totalPoints = calculateTotalPoints(match),
                            uploadStamp = null
                        )
                    }*/
                    match.copy(
                        userId = authUseCases.getUserId(),
                        createStamp = if (match.createStamp == 0L) System.currentTimeMillis() else match.createStamp,
                        editStamp = System.currentTimeMillis(),
                        totalPoints = calculateTotalPoints(match),
                        uploadStamp = null
                    )
                )
                currentKey = match.key
                isNewMatch.update { false }
                getMatch(currentKey)
            }
        }
    }

    fun edit() {
        isEditEnabled.update { true }
        job?.cancel()
    }

    fun reset() {
        state.update { oldMatch ->
            Match(
                userId = oldMatch.userId,
                key = oldMatch.key,
                createStamp = oldMatch.createStamp,
                editStamp = oldMatch.editStamp,
                uploadStamp = oldMatch.uploadStamp,
            )
        }
    }

    private fun getMatch(key: String?) {
        key?.let {
            job?.cancel()
            job = viewModelScope.launch {
                matchUseCases.getMatch(it).collectLatest { newMatch ->
                    if (newMatch != null) {
                        state.update { newMatch }
                    } else {
                        state.update { Match() }
                    }
                }
            }
        }
    }

    private fun calculateTotalPoints(it: Match): Int {
        val autoPoints = it.autoTerminal +
                it.autoGroundJunction * 2 +
                it.autoLowJunction * 3 +
                it.autoMediumJunction * 4 +
                it.autoHighJunction * 5 +
                when (it.autoParked1) {
                    false -> 2
                    true -> 10 * (it.customSignalSleeve1 + 1)
                    else -> 0
                } +
                when (it.autoParked2) {
                    false -> 2
                    true -> 10 * (it.customSignalSleeve2 + 1)
                    else -> 0
                } * it.twoTeams
        val driverPoints = it.driverTerminal +
                it.driverGroundJunction * 2 +
                it.driverLowJunction * 3 +
                it.driverMediumJunction * 4 +
                it.driverHighJunction * 5
        val endgamePoints = it.junctionsOwnedByCone * 3 +
                it.junctionsOwnedByBeacons * 10 +
                it.circuitCompleted * 20 +
                it.endParked1 * 2 +
                it.endParked2 * 2 * it.twoTeams
        return autoPoints + driverPoints + endgamePoints
    }
}