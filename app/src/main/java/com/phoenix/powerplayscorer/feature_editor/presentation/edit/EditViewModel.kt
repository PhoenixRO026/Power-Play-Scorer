package com.phoenix.powerplayscorer.feature_editor.presentation.edit

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
class EditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val matchUseCases: MatchUseCases,
    private val authUseCases: AuthUseCases
): ViewModel() {
    private var currentKey: String? = savedStateHandle.get<String>("key")

    private var job: Job? = null

    private val _matchState = MutableStateFlow(
        savedStateHandle.get<Match>("matchState") ?: Match()
    )
    val matchState = _matchState.asStateFlow()

    private val _isNewMatch = MutableStateFlow(
        savedStateHandle.get<Boolean>("isNewMatch") ?: (currentKey == null)
    )
    val isNewMatch = _isNewMatch.asStateFlow()

    private val _editEnabled = MutableStateFlow(
        savedStateHandle.get<Boolean>("editEnabled") ?: (currentKey == null)
    )
    val editEnabled = _editEnabled.asStateFlow()

    init {
        updateSaveStateHandle()
        getMatch(currentKey)
    }

    fun onTwoTeams() {
        _matchState.update { match ->
            match.copy(
                twoTeams = match.twoTeams.not()
            )
        }
    }

    fun reset() {
        _matchState.update { match ->
            Match(
                userId = match.userId,
                key = match.key,
                createStamp = match.createStamp,
                editStamp = match.editStamp,
                uploadStamp = match.uploadStamp,
            )
        }
    }

    private fun updateSaveStateHandle() {
        matchState.onEach {
            savedStateHandle["matchState"] = it
        }.launchIn(viewModelScope)
        isNewMatch.onEach {
            savedStateHandle["isNewMatch"] = it
        }.launchIn(viewModelScope)
        editEnabled.onEach {
            savedStateHandle["editEnabled"] = it
        }.launchIn(viewModelScope)
    }

    private fun getMatch(key: String?) {
        key?.let {
            job?.cancel()
            job = viewModelScope.launch {
                matchUseCases.getMatch(it).collectLatest { newMatch ->
                    if (newMatch != null) {
                        _matchState.update {
                            newMatch
                        }
                    } else {
                        _matchState.update {
                            Match()
                        }
                    }
                }
            }
        }
    }
}