package com.innovagab.app.data.ideas

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class IdeaStatus(val key: String, val label: String) {
    PENDING("pending", "Em análise"),
    APPROVED("approved", "Aprovada"),
    IN_PROGRESS("in_progress", "Em execução"),
    REJECTED("rejected", "Rejeitada"),
    COMPLETED("completed", "Concluída");

    companion object {
        fun fromKey(key: String): IdeaStatus = entries.find { it.key == key } ?: PENDING
    }
}

enum class IdeaPriority(val key: String, val label: String) {
    LOW("low", "Baixa"),
    MEDIUM("medium", "Média"),
    HIGH("high", "Alta");

    companion object {
        fun fromKey(key: String): IdeaPriority = entries.find { it.key == key } ?: MEDIUM
    }
}

val ideaCategories = listOf("Processo", "Produto", "Tecnologia", "Pessoas", "Cliente", "Outro")
val ideaImpacts = listOf("Baixo", "Médio", "Alto")

data class Idea(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val impact: String = "",
    val status: IdeaStatus = IdeaStatus.PENDING,
    val priority: IdeaPriority = IdeaPriority.MEDIUM,
    val authorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val comment: String = "",
) {
    fun formattedDate(): String = runCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).format(formatter)
    }.getOrDefault("--/--/----")

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "category" to category,
        "impact" to impact,
        "status" to status.key,
        "priority" to priority.key,
        "authorId" to authorId,
        "createdAt" to createdAt,
        "comment" to comment,
    )
}
