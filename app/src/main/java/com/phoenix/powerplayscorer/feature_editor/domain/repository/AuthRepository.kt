package com.phoenix.powerplayscorer.feature_editor.domain.repository

import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    fun isUserSignedIn(): Boolean

    fun loginOnline(email: String, password: String): Flow<Response<Unit>>

    fun register(email: String, password: String): Flow<Response<Unit>>

    fun getUserId(): String

    fun signOut(): Flow<Response<Unit>>

    fun signInOffline(): Flow<Response<Unit>>

    fun getUidFlow(): StateFlow<String?>
}