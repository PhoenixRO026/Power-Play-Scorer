package com.phoenix.powerplayscorer.feature_editor.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
): ViewModel() {
    fun signOut(
        onSuccess: () -> Unit,
        onFailure: (String?) -> Unit
    ) {
        viewModelScope.launch {
            authUseCases.signOut().collect() { response ->
                when (response) {
                    is Response.Loading -> Unit//state.update { it.copy(isRegisterLoading = true) }
                    is Response.Success -> {
                        //state.update { it.copy(isRegisterLoading = false) }
                        onSuccess()
                    }
                    is Response.Failure -> {
                        //state.update { it.copy(isRegisterLoading = false) }
                        onFailure(response.message)
                    }
                }
            }
        }
    }
}