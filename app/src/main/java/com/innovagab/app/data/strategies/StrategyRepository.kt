package com.innovagab.app.data.strategies

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class StrategyRepository {

    private val collection = FirebaseFirestore.getInstance().collection("strategies")

    fun getAllStrategiesStream(): Flow<Result<List<Strategy>>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            val strategies = snapshot?.documents
                ?.mapNotNull { it.toStrategy() }
                ?.sortedByDescending { it.createdAt }
                ?: emptyList()
            trySend(Result.success(strategies))
        }
        awaitClose { listener.remove() }
    }

    fun getStrategyStream(strategyId: String): Flow<Result<Strategy?>> = callbackFlow {
        val listener = collection.document(strategyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                trySend(Result.success(snapshot?.toStrategy()))
            }
        awaitClose { listener.remove() }
    }

    suspend fun createStrategy(strategy: Strategy): Result<Unit> = try {
        val docRef = collection.document()
        docRef.set(strategy.copy(id = docRef.id).toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao criar estratégia. Tente novamente."))
    }

    suspend fun updateStrategy(strategyId: String, updates: Map<String, Any>): Result<Unit> = try {
        collection.document(strategyId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao atualizar estratégia. Tente novamente."))
    }

    suspend fun deleteStrategy(strategyId: String): Result<Unit> = try {
        collection.document(strategyId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Erro ao remover estratégia. Tente novamente."))
    }
}

private fun DocumentSnapshot.toStrategy(): Strategy? = runCatching {
    Strategy(
        id = id,
        title = getString("title") ?: "",
        description = getString("description") ?: "",
        pillar = StrategyPillar.fromKey(getString("pillar") ?: ""),
        status = StrategyStatus.fromKey(getString("status") ?: ""),
        priority = StrategyPriority.fromKey(getString("priority") ?: ""),
        cycle = getString("cycle") ?: "",
        deadline = getString("deadline") ?: "",
        owner = getString("owner") ?: "",
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
    )
}.getOrNull()
