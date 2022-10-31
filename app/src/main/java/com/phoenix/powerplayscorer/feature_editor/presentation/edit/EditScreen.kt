package com.phoenix.powerplayscorer.feature_editor.presentation.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.presentation.edit.components.TopAppBar

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel()
) {
    val match by viewModel.matchState.collectAsStateWithLifecycle()
    val isNewMatch by viewModel.isNewMatch.collectAsStateWithLifecycle()
    val editEnabled by viewModel.editEnabled.collectAsStateWithLifecycle()
    EditScreen(
        match = match,
        isNewMatch = isNewMatch,
        editEnabled = editEnabled,
        onChecked = { viewModel.onTwoTeams() },
        onReset = { viewModel.reset() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditScreen(
    match: Match,
    isNewMatch: Boolean,
    editEnabled: Boolean,
    onChecked: () -> Unit,
    onReset: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                checked = match.twoTeams,
                onCheckedChange = onChecked,
                onReset = onReset,
                editEnabled = editEnabled,
                isNewMatch = isNewMatch
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}