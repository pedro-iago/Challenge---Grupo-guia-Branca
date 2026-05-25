package com.innovagab.app.data.projects

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ProjectStatus(val key: String, val label: String) {
    ACTIVE("active", "Em andamento"),
    ON_HOLD("on_hold", "Em pausa"),
    COMPLETED("completed", "Concluído");

    companion object {
        fun fromKey(key: String): ProjectStatus = entries.find { it.key == key } ?: ACTIVE
    }
}

data class Project(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val ideaId: String = "",
    val status: ProjectStatus = ProjectStatus.ACTIVE,
    val progress: Float = 0f,
    val roi: Double = 0.0,
    val investment: Double = 0.0,
    val deadline: String = "",
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun formattedDate(): String = runCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()).format(formatter)
    }.getOrDefault("--/--/----")

    fun progressPercent(): Int = (progress * 100).toInt()

    fun formattedInvestment(): String {
        if (investment <= 0.0) return "—"
        return when {
            investment >= 1_000_000 -> "R$ %.1fM".format(investment / 1_000_000)
            investment >= 1_000 -> "R$ %.0fk".format(investment / 1_000)
            else -> "R$ %.0f".format(investment)
        }
    }

    fun formattedRoi(): String {
        if (roi == 0.0) return "—"
        return "%s%.1f%%".format(if (roi > 0) "+" else "", roi)
    }

    fun daysSinceCreation(): Int =
        ((System.currentTimeMillis() - createdAt) / 86_400_000L).toInt().coerceAtLeast(0)

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "ideaId" to ideaId,
        "status" to status.key,
        "progress" to progress.toDouble(),
        "roi" to roi,
        "investment" to investment,
        "deadline" to deadline,
        "createdAt" to createdAt,
    )
}
