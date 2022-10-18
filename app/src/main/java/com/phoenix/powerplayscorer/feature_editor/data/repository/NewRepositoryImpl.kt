package com.phoenix.powerplayscorer.feature_editor.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.feature_editor.data.data_source.MatchDao
import com.phoenix.powerplayscorer.feature_editor.data.data_source.User
import com.phoenix.powerplayscorer.feature_editor.domain.model.*
import com.phoenix.powerplayscorer.feature_editor.domain.repository.Repository
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await

class NewRepositoryImpl(
    private val dao: MatchDao,
    private val authUseCases: AuthUseCases
): Repository {

    private val db = Firebase.firestore
    private var newMatchListener: ListenerRegistration? = null
    private var deletedMatchesListener: ListenerRegistration? = null
    private val handler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is CustomException)
            return@CoroutineExceptionHandler
        Log.e(TAG, "Firebase sync failed.", throwable)
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
    private var uploadJob: Job? = null
    private var updateJob: Job? = null
    private var deleteJob: Job? = null

    init {
        scope.launch {
            authUseCases.getUserIdFlow().collectLatest { _uid ->
                deletedMatchesListener?.remove()
                newMatchListener?.remove()
                uploadJob?.cancel()
                updateJob?.cancel()
                deleteJob?.cancel()
                _uid?.let { uid ->
                    startUp(uid)
                    uploadJob = launch {
                        upload(uid, this)
                    }
                    updateJob = launch {
                        update(uid, this)
                    }
                    deleteJob = launch {
                        delete(uid, this)
                    }
                    deletedMatchesListener = deletedMatchesListener(uid, this)
                    val latestStamp = dao.getLatestUploadStamp(uid)
                    newMatchListener = newMatchesListener(latestStamp, uid, this)
                }
            }
        }
    }

    private suspend fun startUp(uid: String) {
        val path = db.collection("users").document(uid)
        if (path.get().await().exists().not()) {
            path.set(User())
        }
    }

    private suspend fun upload(
        id: String,
        scope: CoroutineScope
    ) {
        dao.getMatchesToBeUploaded(id).collect { matches ->
            if (matches.isEmpty()) return@collect
            val uRef = db.collection("users").document(id)
            for (match in matches) {
                scope.launch {
                    val batch = db.batch()
                    val mRef = uRef.collection("matches").document(match.key)
                    batch.update(uRef, "matchesIds", FieldValue.arrayUnion(match.key))
                    batch.set(mRef, match.toFirebaseMatch())
                    Log.e(TAG, "Attempting to upload a match")
                    try {
                        batch.commit().await()
                        Log.e(TAG, "Uploaded a new batch")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to upload a match")
                        throw e
                    }
                }
            }
        }
    }

    private suspend fun update(id: String, scope: CoroutineScope) {
        dao.getMatchesToBeUpdated(id).collect { matches ->
            if (matches.isEmpty()) return@collect

            val uRef = db.collection("users").document(id)
            for (match in matches) {
                scope.launch {
                    val mRef = uRef.collection("matches").document(match.key)
                    Log.e(TAG, "Attempting to update a match")
                    try {
                        mRef.set(match.toFirebaseMatch()).await()
                        Log.e(TAG, "Match update successful")
                    } catch (e: Exception) {
                        Log.e(TAG, "Match update failed", e)
                        Log.e(TAG, "Sending match to upload")
                        dao.insertMatch(
                            match.copy(
                                status = 0
                            )
                        )
                        //throw e
                    }
                }
            }
        }
    }

    private suspend fun delete(
        id: String,
        scope: CoroutineScope
    ) {
        dao.getDeletedMatchesFlow(id).collect { matches ->
            if (matches.isEmpty()) return@collect
            val uRef = db.collection("users").document(id)
            for (match in matches) {
                scope.launch {
                    val batch = db.batch()
                    val mRef = uRef.collection("matches").document(match.key)
                    batch.update(uRef, "matchesIds", FieldValue.arrayRemove(match.key))
                    batch.delete(mRef)
                    Log.e(TAG, "Attempting to delete a match")
                    try {
                        batch.commit().await()
                        Log.e(TAG, "Deleted a batch")
                    } catch (e: Exception) {
                        Log.e(TAG, "Match delete failed")
                        throw e
                    }
                }
            }
        }
    }

    private fun deletedMatchesListener(uid: String, scope: CoroutineScope): ListenerRegistration {
        val path = db.collection("users").document(uid)
        return path.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Deleted matches listener failed", e)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            if (snapshot.exists().not()) {
                path.set(User())
                return@addSnapshotListener
            }
            if (snapshot.metadata.hasPendingWrites()) return@addSnapshotListener
            Log.e(TAG, "New deleted matches snapshot")
            val onlineMatchList = snapshot.toObject<User>()?.matchesIds ?: emptyList()
            scope.launch {
                val uploadedMatches = dao.getUploadedMatchesKeys(uid)
                val deletedMatches = mutableListOf<String>()
                for (match in uploadedMatches) {
                    if (onlineMatchList.contains(match).not()) {
                        deletedMatches.add(match)
                    }
                }
                if (deletedMatches.isEmpty()) return@launch
                dao.deleteMatchListByKeys(deletedMatches)
                Log.e(TAG, "Deleted matches")
            }
        }
    }

    private fun newMatchesListener(latestStamp: Long?, id: String, scope: CoroutineScope): ListenerRegistration {
        val path = db.collection("users").document(id).collection("matches")
        Log.e(TAG, "Latest stamp: $latestStamp")
        return path.whereGreaterThan(
            "uploadStamp",
            (latestStamp ?: 0).toTimestamp()
        ).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            if (snapshot.isEmpty) return@addSnapshotListener
            val newMatches = mutableListOf<Match>()
            Log.e(TAG, "New matches snapshot with ${snapshot.documentChanges.size} changes")
            for (doc in snapshot.documentChanges) {
                if (doc.document.metadata.hasPendingWrites().not()) {
                    when (doc.type) {
                        DocumentChange.Type.ADDED -> newMatches.add(doc.document.toObject<FirebaseMatch>().toMatch(doc.document.id, id))
                        DocumentChange.Type.MODIFIED -> newMatches.add(doc.document.toObject<FirebaseMatch>().toMatch(doc.document.id, id))
                        else -> println("idk")
                    }
                }
            }
            scope.launch {
                Log.e(TAG, "$newMatches")
                dao.insertMatches(newMatches)
            }
        }
    }

    /*override fun getMatches(): Flow<List<Match>> = flow {
        authUseCases.getUserIdFlow().collect { _uid ->
            _uid?.let { uid ->
                dao.getMatches(uid).collect { matchList ->
                    Log.e(TAG, "New emit with uid: $uid")
                    emit(matchList)
                }
            }
        }
    }*/
    override fun getMatches(): Flow<List<Match>> {
        return dao.getMatches(authUseCases.getUserId())
    }

    override fun getMatchByKey(key: String): Flow<Match?> {
        return dao.getMatchByKey(key)
    }

    override suspend fun insertMatch(match: Match) {
        return dao.insertMatch(
            match.copy(
                status = if (match.status == 1) 2 else 0
            )
        )
    }

    override suspend fun insertMatches(matchList: List<Match>) {
        return dao.insertMatches(matchList)
    }

    override suspend fun deleteMatches(matchList: List<Match>) {
        val onlineMatches = mutableListOf<Match>()
        val offlineMatches = mutableListOf<Match>()
        for (match in matchList) {
            if (match.userId == "offline") {
                offlineMatches.add(match)
            } else {
                onlineMatches.add(match)
            }
        }
        dao.insertMatches(
            onlineMatches.map {
                it.copy(
                    toBeDeleted = true
                )
            }
        )
        dao.deleteMatches(offlineMatches)
    }
}