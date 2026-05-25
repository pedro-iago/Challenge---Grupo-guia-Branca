package com.innovagab.app.data.auth

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
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val profile = UserProfile(
                uid = uid,
                name = doc.getString("name") ?: auth.currentUser?.displayName ?: "Usuário",
                email = doc.getString("email") ?: auth.currentUser?.email ?: "",
                role = UserRole.fromKey(doc.getString("role") ?: UserRole.OPERADOR.key),
            )
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao carregar perfil. Tente novamente."))
        }
    }

    fun signOut() = auth.signOut()
}
