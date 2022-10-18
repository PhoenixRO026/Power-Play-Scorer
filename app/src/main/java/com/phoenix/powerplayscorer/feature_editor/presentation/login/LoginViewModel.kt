package com.phoenix.powerplayscorer.feature_editor.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
): ViewModel() {
    val state = MutableStateFlow(LoginState())

    fun login(
        onSuccess: () -> Unit,
        onFailure: (message: String?) -> Unit
    ) {
        viewModelScope.launch {
            state.value.let { loginState ->
                authUseCases.loginOnline(
                    email = loginState.email,
                    password = loginState.password
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> state.update { it.copy(isLoginLoading = true) }
                        is Response.Success -> {
                            state.update { it.copy(isLoginLoading = false) }
                            onSuccess()
                        }
                        is Response.Failure -> {
                            state.update { it.copy(isLoginLoading = false) }
                            onFailure(response.message)
                        }
                    }
                }
            }
        }
    }

    fun register(
        onSuccess: () -> Unit,
        onFailure: (message: String?) -> Unit
    ) {
        viewModelScope.launch {
            state.value.let { loginState ->
                authUseCases.register(
                    email = loginState.email,
                    password = loginState.password
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> state.update { it.copy(isRegisterLoading = true) }
                        is Response.Success -> {
                            state.update { it.copy(isRegisterLoading = false) }
                            onSuccess()
                        }
                        is Response.Failure -> {
                            state.update { it.copy(isRegisterLoading = false) }
                            onFailure(response.message)
                        }
                    }
                }
            }
        }
    }
}