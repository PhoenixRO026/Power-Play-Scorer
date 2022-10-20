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

class RepositoryImpl(
    private val dao: MatchDao,
    private val authUseCases: AuthUseCases
): Repository {

    private val db = Firebase.firestore
    private var newMatchListener: ListenerRegistration? = null
    private var deletedMatchesListener: ListenerRegistration? = null
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Firebase sync failed.", throwable)
    }
    private val globalScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)

    init {
        userCollection { uid: String ->
            launch {
                deletedMatchesListener = deletedMatchesListener(uid, this@userCollection)
                val latestStamp = dao.getLatestUploadStamp(uid)
                newMatchListener = newMatchesListener(latestStamp, uid, this@userCollection)
            }
            launch {
                startUp(uid)
                upload(uid)
                update(uid)
                deleteUploadedMatches(uid)
                deleteFailedMatches(uid)
            }
        }
    }

    private fun userCollection(
        block: CoroutineScope.(uid: String) -> Unit
    ) {
        var cancelScope: CoroutineScope? = null
        globalScope.launch {
            authUseCases.getUserIdFlow().collectLatest { _uid ->
                removeAndCancelJobs(cancelScope)
                _uid?.let { uid ->
                    supervisorScope {
                        cancelScope = this
                        this.block(uid)
                    }
                }
            }
        }
    }

    private fun removeAndCancelJobs(cancelScope: CoroutineScope?) {
        deletedMatchesListener?.remove()
        newMatchListener?.remove()
        cancelScope?.cancel()
    }

    private suspend fun startUp(uid: String) {
        val path = db.collection("users").document(uid)
        if (path.get().await().exists().not()) {
            path.set(User())
        }
    }

    private fun CoroutineScope.upload(
        id: String
    ) {
        launch {
            dao.getMatchesToBeUploaded(id).collect { matches ->
                if (matches.isEmpty()) return@collect
                val uRef = db.collection("users").document(id)
                for (match in matches) {
                    launch {
                        val batch = db.batch()
                        val mRef = uRef.collection("matches").document(match.key)
                        batch.update(uRef, "matchesIds", FieldValue.arrayUnion(match.key))
                        batch.set(mRef, match.toFirebaseMatch())
                        Log.i(TAG, "Attempting to upload a match")
                        try {
                            batch.commit().await()
                            Log.i(TAG, "Uploaded a new batch")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to upload a match")
                            dao.insertMatch(
                                match.copy(
                                    status = 3
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.update(id: String) {
        launch {
            dao.getMatchesToBeUpdated(id).collect { matches ->
                if (matches.isEmpty()) return@collect
                val uRef = db.collection("users").document(id)
                for (match in matches) {
                    launch {
                        val mRef = uRef.collection("matches").document(match.key)
                        Log.i(TAG, "Attempting to update a match")
                        try {
                            mRef.set(match.toFirebaseMatch()).await()
                            Log.i(TAG, "Match update successful")
                        } catch (e: Exception) {
                            Log.e(TAG, "Match update failed, sending match to upload", e)
                            dao.insertMatch(
                                match.copy(
                                    status = 0
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.deleteFailedMatches(id: String) {
        launch {
            dao.getFailedDeletedMatchesFlow(id).collect {
                dao.deleteMatches(it)
            }
        }
    }

    private fun CoroutineScope.deleteUploadedMatches(
        id: String
    ) {
        launch {
            dao.getOnlineDeletedMatchesFlow(id).collect { matches ->
                if (matches.isEmpty()) return@collect
                val uRef = db.collection("users").document(id)
                for (match in matches) {
                    launch {
                        val batch = db.batch()
                        val mRef = uRef.collection("matches").document(match.key)
                        batch.update(uRef, "matchesIds", FieldValue.arrayRemove(match.key))
                        batch.delete(mRef)
                        Log.i(TAG, "Attempting to deleteUploadedMatches a match")
                        try {
                            batch.commit().await()
                            Log.i(TAG, "Deleted a batch")
                        } catch (e: Exception) {
                            Log.e(TAG, "Match deleteUploadedMatches failed")
                            dao.insertMatch(
                                match.copy(
                                    status = 4
                                )
                            )
                        }
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
            Log.e(TAG, "snapshot sorting done")
            scope.launch {
                Log.e(TAG, "$newMatches")
                dao.insertMatches(newMatches)
            }
        }
    }

    override fun getMatches(): Flow<List<Match>> {
        return dao.getMatches(authUseCases.getUserId())
    }

    override fun getMatchByKey(key: String): Flow<Match?> {
        return dao.getMatchByKey(key)
    }

    override suspend fun insertMatch(match: Match) {
        dao.insertMatch(match)
    }

    override suspend fun insertMatches(matchList: List<Match>) {
        return dao.insertMatches(matchList)
    }

    override suspend fun deleteMatches(matchList: List<Match>) {
        dao.deleteMatches(matchList)
    }
}