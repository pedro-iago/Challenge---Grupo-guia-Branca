package com.innovagab.app.data.gamification

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class BadgeType(
    val key: String,
    val title: String,
    val description: String,
    val isManual: Boolean = false,
) {
    PIONEIRO(
        key = "pioneiro",
        title = "Pioneiro",
        description = "Contribuiu com a primeira ideia à plataforma",
    ),
    INOVADOR(
        key = "inovador",
        title = "Inovador",
        description = "Submeteu 5 ou mais ideias",
    ),
    SOLUCIONADOR(
        key = "solucionador",
        title = "Solucionador",
        description = "Teve 2 ou mais ideias aprovadas ou em execução",
    ),
    TRANSFORMADOR(
        key = "transformador",
        title = "Transformador",
        description = "Concluiu pelo menos um projeto de inovação",
    ),
    COLABORADOR(
        key = "colaborador",
        title = "Colaborador",
        description = "Manteve contribuições ativas nos últimos 30 dias",
    ),
    DESTAQUE_MES(
        key = "destaque_mes",
        title = "Destaque do Mês",
        description = "Reconhecido pela liderança como maior contribuidor do período",
        isManual = true,
    ),
    AGENTE_MUDANCA(
        key = "agente_mudanca",
        title = "Agente de Mudança",
        description = "Reconhecido por impulsionar transformações organizacionais",
        isManual = true,
    ),
    LIDER_INOVACAO(
        key = "lider_inovacao",
        title = "Líder de Inovação",
        description = "Excelência e consistência em contribuições estratégicas",
        isManual = true,
    );

    companion object {
        fun fromKey(key: String): BadgeType? = entries.find { it.key == key }
        val manualBadges: List<BadgeType> get() = entries.filter { it.isManual }
    }
}

data class Recognition(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val badge: BadgeType = BadgeType.DESTAQUE_MES,
    val note: String = "",
    val grantedBy: String = "",
    val grantedAt: Long = System.currentTimeMillis(),
) {
    fun formattedDate(): String = runCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        Instant.ofEpochMilli(grantedAt).atZone(ZoneId.systemDefault()).format(formatter)
    }.getOrDefault("--/--/----")

    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "userName" to userName,
        "badge" to badge.key,
        "note" to note,
        "grantedBy" to grantedBy,
        "grantedAt" to grantedAt,
    )
}

data class UserScore(
    val userId: String = "",
    val userName: String = "",
    val points: Int = 0,
    val totalIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val completedIdeas: Int = 0,
    val autoBadges: Set<BadgeType> = emptySet(),
    val manualBadges: Set<BadgeType> = emptySet(),
) {
    val allBadges: Set<BadgeType> get() = autoBadges + manualBadges
    val initials: String
        get() = userName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
