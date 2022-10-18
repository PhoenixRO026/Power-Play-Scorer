package com.phoenix.powerplayscorer.feature_editor.presentation.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val passVisible: Boolean = false,
    val isLoginLoading: Boolean = false,
    val isRegisterLoading: Boolean = false
)
