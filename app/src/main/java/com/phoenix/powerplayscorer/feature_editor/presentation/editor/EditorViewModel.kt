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

    val savableState = MutableStateFlow(
        savedStateHandle.get<EditorState>("editorState") ?: EditorState(
            Match(),
            currentKey == null,
            currentKey == null
        )
    )

    val state = savableState.map { editorState: EditorState ->
        MappedEditorState(
            match = editorState.match,
            isEditEnabled = editorState.isEditEnabled,
            isNewMatch = editorState.isNewMatch,
            matchLimits = MatchLimits(editorState.match),
            matchTotals = MatchTotals(editorState.match),
            twoTeams = editorState.match.twoTeams,
            matchVisibility = MatchVisibiliy(editorState.match)
        )
    }

    private var job: Job? = null

    init {
        updateSaveStateHandle()
        getMatch(currentKey)
    }

    private fun updateSaveStateHandle() {
        savableState.onEach {
            savedStateHandle["editorState"] = it
        }.launchIn(viewModelScope)
    }

    fun save() {
        savableState.update { editorState ->
            editorState.copy(
                isEditEnabled = false
            )
        }
        viewModelScope.launch {
            savableState.value.let { editorState ->
                val match = editorState.match
                matchUseCases.saveMatch(
                    match.copy(
                        userId = authUseCases.getUserId(),
                        createStamp = if (match.createStamp == 0L) System.currentTimeMillis() else match.createStamp,
                        editStamp = System.currentTimeMillis(),
                        totalPoints = calculateTotalPoints(match),
                        uploadStamp = null
                    )
                )
                currentKey = match.key
                savableState.update { editorState2 ->
                    editorState2.copy(
                        isNewMatch = false
                    )
                }
                getMatch(currentKey)
            }
        }
    }

    fun edit() {
        savableState.update { editorState ->
            editorState.copy(
                isEditEnabled = false
            )
        }
        job?.cancel()
    }

    fun reset() {
        savableState.update { editorState ->
            val oldMatch = editorState.match
            editorState.copy(
                match = Match(
                    userId = oldMatch.userId,
                    key = oldMatch.key,
                    createStamp = oldMatch.createStamp,
                    editStamp = oldMatch.editStamp,
                    uploadStamp = oldMatch.uploadStamp,
                )
            )
        }
    }

    private fun getMatch(key: String?) {
        key?.let {
            job?.cancel()
            job = viewModelScope.launch {
                matchUseCases.getMatch(it).collectLatest { newMatch ->
                    if (newMatch != null) {
                        savableState.update { editorState ->
                            editorState.copy(
                                match = newMatch
                            )
                        }

                    } else {
                        savableState.update { editorState ->
                            editorState.copy(
                                match = Match()
                            )
                        }
                    }
                }
            }
        }
    }
}