package com.phoenix.powerplayscorer.feature_editor.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: AuthRepository {

    private val currentAuth = MutableStateFlow(Firebase.auth)
    private val currentUser = MutableStateFlow(Firebase.auth.currentUser)
    private val currentUid = MutableStateFlow(Firebase.auth.currentUser?.uid)

    init {
        Firebase.auth.addAuthStateListener {  newAuth ->
            currentAuth.update { newAuth }
            currentUser.update { newAuth.currentUser }
            currentUid.update { newAuth.currentUser?.uid }
        }
    }

    override fun isUserSignedIn(): Boolean = currentUser.value != null

    override fun loginOnline(email: String, password: String): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            currentAuth.value.signInWithEmailAndPassword(email, password).await()
            emit(Response.Success(Unit))
        } catch(e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun register(email: String, password: String): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            currentAuth.value.createUserWithEmailAndPassword(email, password).await()
            emit(Response.Success(Unit))
        } catch(e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun getUserId(): String {
        return currentUid.value ?: "offline"
    }

    override fun signOut(): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            currentAuth.value.signOut()
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun signInOffline(): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun getUidFlow(): StateFlow<String?> {
        return currentUid.asStateFlow()
    }
}