package com.innovagab.app.data.ideas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class IdeaRepository {

    private val auth = FirebaseAuth.getInstance()
    private val collection = FirebaseFirestore.getInstance().collection("ideas")

    val currentUserId: String? get() = auth.currentUser?.uid

    fun getUserIdeasStream(userId: String): Flow<Result<List<Idea>>> = callbackFlow {
        val listener = collection
            .whereEqualTo("authorId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val ideas = snapshot?.documents
                    ?.mapNotNull { it.toIdea() }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                trySend(Result.success(ideas))
            }
        awaitClose { listener.remove() }
    }

    fun getIdeaStream(ideaId: String): Flow<Result<Idea?>> = callbackFlow {
        val listener = collection.document(ideaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                trySend(Result.success(snapshot?.toIdea()))
            }
        awaitClose { listener.remove() }
    }

    suspend fun createIdea(idea: Idea): Result<Unit> = try {
        val docRef = collection.document()
        val ideaWithId = idea.copy(id = docRef.id, authorId = currentUserId ?: "")
        docRef.set(ideaWithId.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao criar ideia. Tente novamente."))
    }

    suspend fun updateStatus(ideaId: String, status: IdeaStatus): Result<Unit> = try {
        collection.document(ideaId).update("status", status.key).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao atualizar status. Tente novamente."))
    }

    fun getAllIdeasStream(): Flow<Result<List<Idea>>> = callbackFlow {
        val listener = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val ideas = snapshot?.documents
                    ?.mapNotNull { it.toIdea() }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                trySend(Result.success(ideas))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateIdeaReview(
        ideaId: String,
        status: IdeaStatus,
        priority: IdeaPriority,
        comment: String,
    ): Result<Unit> = try {
        collection.document(ideaId).update(
            mapOf(
                "status" to status.key,
                "priority" to priority.key,
                "comment" to comment,
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao atualizar ideia. Tente novamente."))
    }

    suspend fun updatePriority(ideaId: String, priority: IdeaPriority): Result<Unit> = try {
        collection.document(ideaId).update("priority", priority.key).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao atualizar prioridade. Tente novamente."))
    }
}

private fun DocumentSnapshot.toIdea(): Idea? = runCatching {
    Idea(
        id = id,
        title = getString("title") ?: "",
        description = getString("description") ?: "",
        category = getString("category") ?: "",
        impact = getString("impact") ?: "",
        status = IdeaStatus.fromKey(getString("status") ?: ""),
        priority = IdeaPriority.fromKey(getString("priority") ?: ""),
        authorId = getString("authorId") ?: "",
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
        comment = getString("comment") ?: "",
    )
}.getOrNull()
