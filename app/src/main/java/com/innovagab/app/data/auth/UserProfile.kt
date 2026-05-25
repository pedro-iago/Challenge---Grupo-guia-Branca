package com.innovagab.app.data.auth

enum class UserRole(val key: String, val label: String) {
    OPERADOR("operador", "Operador"),
    GESTOR("gestor", "Gestor"),
    LIDERANCA("lideranca", "Liderança");

    companion object {
        fun fromKey(key: String): UserRole =
            entries.find { it.key == key.lowercase() } ?: OPERADOR
    }
}

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val role: UserRole,
)
