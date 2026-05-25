package com.innovagab.app.data.strategies

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class StrategyStatus(val key: String, val label: String) {
    ACTIVE("active", "Ativa"),
    DRAFT("draft", "Rascunho"),
    ARCHIVED("archived", "Arquivada");

    companion object {
        fun fromKey(key: String): StrategyStatus = entries.find { it.key == key } ?: DRAFT
    }
}

enum class StrategyPillar(val key: String, val label: String) {
    GROWTH("growth", "Crescimento"),
    INNOVATION("innovation", "Inovação"),
    PEOPLE("people", "Pessoas"),
    SUSTAINABILITY("sustainability", "Sustentabilidade"),
    EFFICIENCY("efficiency", "Eficiência"),
    CUSTOMER("customer", "Cliente");

    companion object {
        fun fromKey(key: String): StrategyPillar = entries.find { it.key == key } ?: GROWTH
    }
}

enum class StrategyPriority(val key: String, val label: String) {
    HIGH("high", "Alta"),
    MEDIUM("medium", "Média"),
    LOW("low", "Baixa");

    companion object {
        fun fromKey(key: String): StrategyPriority = entries.find { it.key == key } ?: MEDIUM
    }
}

data class Strategy(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pillar: StrategyPillar = StrategyPillar.GROWTH,
    val status: StrategyStatus = StrategyStatus.DRAFT,
    val priority: StrategyPriority = StrategyPriority.MEDIUM,
    val cycle: String = "",
    val deadline: String = "",
    val owner: String = "",
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun formattedDate(): String = runCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).format(formatter)
    }.getOrDefault("--/--/----")

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "pillar" to pillar.key,
        "status" to status.key,
        "priority" to priority.key,
        "cycle" to cycle,
        "deadline" to deadline,
        "owner" to owner,
        "createdAt" to createdAt,
    )
}
