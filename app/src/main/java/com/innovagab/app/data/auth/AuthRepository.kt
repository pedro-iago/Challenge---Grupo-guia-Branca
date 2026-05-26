package com.innovagab.app.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<UserProfile> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid
                ?: return Result.failure(Exception("Erro de autenticação. Tente novamente."))
            fetchProfile(uid)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Credenciais inválidas. Verifique e-mail e senha."))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Usuário não encontrado."))
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Sem conexão. Verifique sua internet."
                e.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true ->
                    "Credenciais inválidas. Verifique e-mail e senha."
                else -> "Erro ao fazer login. Tente novamente."
            }
            Result.failure(Exception(message))
        }
    }

    suspend fun fetchCurrentProfile(): Result<UserProfile> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Sessão expirada. Faça login novamente."))
        return fetchProfile(uid)
    }

    private suspend fun fetchProfile(uid: String): Result<UserProfile> {
        val email = auth.currentUser?.email ?: ""
        val displayName = auth.currentUser?.displayName

        // Role derivado do email — funciona mesmo sem Firestore
        val fallbackRole = when {
            email.startsWith("operador") -> UserRole.OPERADOR
            email.startsWith("gestor") -> UserRole.GESTOR
            email.startsWith("lideranca") -> UserRole.LIDERANCA
            else -> UserRole.OPERADOR
        }
        val fallbackName = displayName
            ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }

        return try {
            val doc = firestore.collection("users").document(uid).get().await()

            val role = UserRole.fromKey(doc.getString("role") ?: fallbackRole.key)
            val name = doc.getString("name") ?: fallbackName

            // Auto-cria documento se não existir (sem await para não bloquear)
            if (!doc.exists()) {
                Log.w("AuthRepository", "Documento não encontrado para uid=$uid — criando...")
                firestore.collection("users").document(uid).set(
                    mapOf("name" to fallbackName, "email" to email, "role" to fallbackRole.key)
                )
            }

            Result.success(UserProfile(uid = uid, name = name, email = email, role = role))
        } catch (e: Exception) {
            // Firestore falhou, mas o usuário está autenticado — faz login com fallback
            Log.e("AuthRepository", "Firestore indisponível, usando fallback. Erro: ${e.message}")
            Result.success(UserProfile(uid = uid, name = fallbackName, email = email, role = fallbackRole))
        }
    }

    fun signOut() = auth.signOut()
}
