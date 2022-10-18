package com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth

data class AuthUseCases(
    val isUserSignedIn: IsUserSignedIn,
    val loginOnline: LoginOnline,
    val register: Register,
    val getUserId: GetUserId,
    val signInOffline: SignInOffline,
    val signOut: SignOut,
    val getUserIdFlow: GetUserIdFlow
)
