package com.innovagab.app.data.projects

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProjectRepository {

    private val collection = FirebaseFirestore.getInstance().collection("projects")

    suspend fun createProject(project: Project): Result<Unit> = try {
        val docRef = collection.document()
        docRef.set(project.copy(id = docRef.id).toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao criar projeto. Tente novamente."))
    }

    fun getAllProjectsStream(): Flow<Result<List<Project>>> = callbackFlow {
        val listener = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val projects = snapshot?.documents
                    ?.mapNotNull { it.toProject() }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                trySend(Result.success(projects))
            }
        awaitClose { listener.remove() }
    }

    fun getProjectStream(projectId: String): Flow<Result<Project?>> = callbackFlow {
        val listener = collection.document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                trySend(Result.success(snapshot?.toProject()))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateProject(projectId: String, updates: Map<String, Any>): Result<Unit> = try {
        collection.document(projectId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao atualizar projeto. Tente novamente."))
    }
}

private fun DocumentSnapshot.toProject(): Project? = runCatching {
    Project(
        id = id,
        title = getString("title") ?: "",
        description = getString("description") ?: "",
        ideaId = getString("ideaId") ?: "",
        status = ProjectStatus.fromKey(getString("status") ?: ""),
        progress = getDouble("progress")?.toFloat() ?: 0f,
        roi = getDouble("roi") ?: 0.0,
        investment = getDouble("investment") ?: 0.0,
        deadline = getString("deadline") ?: "",
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
    )
}.getOrNull()
