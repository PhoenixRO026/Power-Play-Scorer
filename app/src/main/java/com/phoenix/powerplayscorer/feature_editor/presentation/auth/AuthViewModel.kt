package com.phoenix.powerplayscorer.feature_editor.presentation.auth

import androidx.lifecycle.ViewModel
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
): ViewModel() {

    fun isUserLoggedIn(): Boolean = authUseCases.isUserSignedIn()

    /*fun singInOffline(
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
    }*/
}