package com.innovagab.app.data.gamification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GamificationRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val recognitionCollection = db.collection("recognition")
    private val usersCollection = db.collection("users")

    val currentUserId: String? get() = auth.currentUser?.uid
    val currentUserName: String get() = auth.currentUser?.displayName ?: "Liderança"

    fun getAllUsersStream(): Flow<Result<List<Pair<String, String>>>> = callbackFlow {
        val listener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            val users = snapshot?.documents?.mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                doc.id to name
            } ?: emptyList()
            trySend(Result.success(users))
        }
        awaitClose { listener.remove() }
    }

    fun getAllRecognitionsStream(): Flow<Result<List<Recognition>>> = callbackFlow {
        val listener = recognitionCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            val recognitions = snapshot?.documents
                ?.mapNotNull { it.toRecognition() }
                ?.sortedByDescending { it.grantedAt }
                ?: emptyList()
            trySend(Result.success(recognitions))
        }
        awaitClose { listener.remove() }
    }

    fun getUserRecognitionsStream(userId: String): Flow<Result<List<Recognition>>> = callbackFlow {
        val listener = recognitionCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val recognitions = snapshot?.documents
                    ?.mapNotNull { it.toRecognition() }
                    ?.sortedByDescending { it.grantedAt }
                    ?: emptyList()
                trySend(Result.success(recognitions))
            }
        awaitClose { listener.remove() }
    }

    suspend fun grantRecognition(
        userId: String,
        userName: String,
        badge: BadgeType,
        note: String,
    ): Result<Unit> = try {
        val docRef = recognitionCollection.document()
        val recognition = Recognition(
            id = docRef.id,
            userId = userId,
            userName = userName,
            badge = badge,
            note = note,
            grantedBy = currentUserName,
        )
        docRef.set(recognition.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao conceder reconhecimento. Tente novamente."))
    }

    suspend fun revokeRecognition(recognitionId: String): Result<Unit> = try {
        recognitionCollection.document(recognitionId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao revogar reconhecimento."))
    }
}

private fun DocumentSnapshot.toRecognition(): Recognition? = runCatching {
    Recognition(
        id = id,
        userId = getString("userId") ?: "",
        userName = getString("userName") ?: "",
        badge = BadgeType.fromKey(getString("badge") ?: "") ?: BadgeType.DESTAQUE_MES,
        note = getString("note") ?: "",
        grantedBy = getString("grantedBy") ?: "",
        grantedAt = getLong("grantedAt") ?: System.currentTimeMillis(),
    )
}.getOrNull()
