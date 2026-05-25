package com.innovagab.app.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.innovagab.app.data.gamification.BadgeType
import com.innovagab.app.data.gamification.UserScore
import com.innovagab.app.features.recognition.AvatarCircle
import com.innovagab.app.features.recognition.BadgeChip
import com.innovagab.app.features.recognition.RecognitionViewModel
import com.innovagab.app.features.recognition.icon
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryCard
import com.innovagab.app.ui.components.AppSurfaceCard
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@Composable
fun HomeScreen(
    userName: String = "Operador",
    viewModel: HomeViewModel = viewModel(),
    recognitionViewModel: RecognitionViewModel? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val leaderboardState by recognitionViewModel?.leaderboardState?.collectAsStateWithLifecycle()
        ?: return@HomeScreen run {
            HomeScreenContent(userName = userName, stats = uiState.stats, userScore = null, rank = 0, totalUsers = 0)
        }

    val currentUserId = recognitionViewModel.currentUserId
    val userScore = leaderboardState.scores.find { it.userId == currentUserId }
    val rank = if (userScore != null) leaderboardState.scores.indexOfFirst { it.userId == currentUserId } + 1 else 0
    val totalUsers = leaderboardState.scores.size

    HomeScreenContent(
        userName = userName,
        stats = uiState.stats,
        userScore = userScore,
        rank = rank,
        totalUsers = totalUsers,
    )
}

@Composable
private fun HomeScreenContent(
    userName: String,
    stats: List<StatItem>,
    userScore: UserScore?,
    rank: Int,
    totalUsers: Int,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item { WelcomeCard(userName = userName, userScore = userScore) }

        if (userScore != null) {
            item { PerformanceSection(score = userScore, rank = rank, totalUsers = totalUsers) }
            if (userScore.allBadges.isNotEmpty()) {
                item { BadgesSection(score = userScore) }
            }
            item { NextAchievementSection(score = userScore) }
        }

        item { StatsSection(stats = stats) }
    }
}

@Composable
private fun WelcomeCard(userName: String, userScore: UserScore?) {
    AppPrimaryCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bem-vindo,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            AvatarCircle(
                initials = userName.split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() },
                color = Color.White,
                size = 44,
            )
        }
        if (userScore != null && userScore.allBadges.isNotEmpty()) {
            Spacer(Modifier.height(Spacing.sm))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = userScore.allBadges.first().icon(),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text = userScore.allBadges.first().title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        } else if (userScore == null) {
            Spacer(Modifier.height(Spacing.sm))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Envie sua primeira ideia e comece a acumular pontos.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f),
            )
        }
    }
}

@Composable
private fun PerformanceSection(score: UserScore, rank: Int, totalUsers: Int) {
    Column {
        SectionTitle("Meu desempenho")
        Spacer(Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            PerformanceCard(
                label = "Posição",
                value = if (rank > 0) "#$rank" else "—",
                sub = if (totalUsers > 0) "de $totalUsers colaboradores" else "",
                modifier = Modifier.weight(1f),
            )
            PerformanceCard(
                label = "Pontuação",
                value = "${score.points}",
                sub = "pontos acumulados",
                modifier = Modifier.weight(1f),
            )
            PerformanceCard(
                label = "Conquistas",
                value = "${score.allBadges.size}",
                sub = "de ${BadgeType.entries.size} disponíveis",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PerformanceCard(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        if (sub.isNotBlank()) {
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BadgesSection(score: UserScore) {
    Column {
        SectionTitle("Minhas conquistas")
        Spacer(Modifier.height(Spacing.sm))
        AppCard {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(score.allBadges.toList()) { badge ->
                    BadgeChip(badge = badge)
                }
            }
            if (score.manualBadges.isNotEmpty()) {
                Spacer(Modifier.height(Spacing.sm))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(Spacing.sm))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(Radius.full),
                    ) {
                        Text(
                            text = "${score.manualBadges.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = "reconhecimento${if (score.manualBadges.size > 1) "s" else ""} da liderança",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun NextAchievementSection(score: UserScore) {
    val next = nextBadgeProgress(score) ?: return

    Column {
        SectionTitle("Próxima conquista")
        Spacer(Modifier.height(Spacing.sm))
        AppSurfaceCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(Radius.sm),
                    modifier = Modifier.size(40.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = next.badge.icon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = next.badge.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = next.hint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    LinearProgressIndicator(
                        progress = { next.progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant,
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${(next.progress * 100).toInt()}% concluído",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private data class BadgeProgress(
    val badge: BadgeType,
    val progress: Float,
    val hint: String,
)

private fun nextBadgeProgress(score: UserScore): BadgeProgress? {
    val earned = score.allBadges
    if (BadgeType.PIONEIRO !in earned) {
        return BadgeProgress(BadgeType.PIONEIRO, 0f, "Envie sua primeira ideia para desbloquear")
    }
    if (BadgeType.COLABORADOR !in earned) {
        return BadgeProgress(BadgeType.COLABORADOR, score.totalIdeas.coerceAtMost(1).toFloat(), "Contribua nos próximos 30 dias para desbloquear")
    }
    if (BadgeType.SOLUCIONADOR !in earned) {
        val progress = (score.approvedIdeas / 2f).coerceIn(0f, 1f)
        val remaining = (2 - score.approvedIdeas).coerceAtLeast(0)
        return BadgeProgress(BadgeType.SOLUCIONADOR, progress, "Faltam $remaining ideia${if (remaining != 1) "s" else ""} aprovada${if (remaining != 1) "s" else ""}")
    }
    if (BadgeType.INOVADOR !in earned) {
        val progress = (score.totalIdeas / 5f).coerceIn(0f, 1f)
        val remaining = (5 - score.totalIdeas).coerceAtLeast(0)
        return BadgeProgress(BadgeType.INOVADOR, progress, "Faltam $remaining ideia${if (remaining != 1) "s" else ""} para desbloquear")
    }
    if (BadgeType.TRANSFORMADOR !in earned) {
        return BadgeProgress(BadgeType.TRANSFORMADOR, 0f, "Tenha uma ideia concluída como projeto")
    }
    return null
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun StatsSection(stats: List<StatItem>) {
    Column {
        SectionTitle("Visão geral")
        Spacer(Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            stats.forEach { stat ->
                AppCard(modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stat.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

