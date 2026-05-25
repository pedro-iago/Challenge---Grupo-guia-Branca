package com.innovagab.app.features.recognition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.gamification.BadgeType
import com.innovagab.app.data.gamification.Recognition
import com.innovagab.app.data.gamification.UserScore
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@Composable
fun UserAchievementsScreen(
    userId: String,
    viewModel: RecognitionViewModel,
) {
    val state by viewModel.leaderboardState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> {
            SkeletonListContent(count = 3)
        }

        else -> {
            val userScore = state.scores.find { it.userId == userId }
            val userRank = state.scores.indexOfFirst { it.userId == userId } + 1
            val userRecognitions = state.recentRecognitions.filter { it.userId == userId }

            if (userScore == null) {
                EmptyState(
                    icon = Icons.Default.Inbox,
                    title = "Dados não encontrados",
                    subtitle = "Não foi possível carregar o perfil deste colaborador.",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                AchievementsContent(
                    score = userScore,
                    rank = userRank,
                    recognitions = userRecognitions,
                )
            }
        }
    }
}

@Composable
private fun AchievementsContent(
    score: UserScore,
    rank: Int,
    recognitions: List<Recognition>,
) {
    val earnedBadges = score.allBadges
    val lockedBadges = BadgeType.entries.filter { it !in earnedBadges }

    LazyColumn(
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item { ProfileHeader(score = score, rank = rank) }
        item { StatsRow(score = score) }

        if (earnedBadges.isNotEmpty()) {
            item {
                SectionLabel(
                    title = "Conquistas obtidas",
                    count = earnedBadges.size,
                )
            }
            item {
                BadgesGrid(
                    badges = earnedBadges.toList(),
                    earned = true,
                    recognitions = recognitions,
                )
            }
        }

        if (lockedBadges.isNotEmpty()) {
            item {
                SectionLabel(
                    title = "Conquistas disponíveis",
                    count = lockedBadges.size,
                )
            }
            item {
                BadgesGrid(
                    badges = lockedBadges,
                    earned = false,
                    recognitions = emptyList(),
                )
            }
        }

        if (recognitions.isNotEmpty()) {
            item { SectionLabel(title = "Reconhecimentos da liderança", count = recognitions.size) }
            items(recognitions, key = { it.id }) { recognition ->
                RecognitionDetailCard(recognition = recognition)
            }
        }

        item { Spacer(Modifier.height(Spacing.xl)) }
    }
}

@Composable
private fun ProfileHeader(score: UserScore, rank: Int) {
    AppPrimaryCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarCircle(
                initials = score.initials,
                color = Color.White,
                size = 56,
            )
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = score.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                if (score.allBadges.isNotEmpty()) {
                    Text(
                        text = score.allBadges.first().title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (rank > 0) "#$rank" else "—",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = "ranking",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
        Spacer(Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HeaderStat(label = "Pontos", value = "${score.points}")
            HeaderStat(label = "Conquistas", value = "${score.allBadges.size}")
            HeaderStat(label = "Reconhec.", value = "${score.manualBadges.size}")
        }
    }
}

@Composable
private fun HeaderStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun StatsRow(score: UserScore) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        StatMiniCard(
            label = "Ideias",
            value = "${score.totalIdeas}",
            modifier = Modifier.weight(1f),
        )
        StatMiniCard(
            label = "Aprovadas",
            value = "${score.approvedIdeas}",
            modifier = Modifier.weight(1f),
        )
        StatMiniCard(
            label = "Concluídas",
            value = "${score.completedIdeas}",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatMiniCard(label: String, value: String, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionLabel(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(Radius.full),
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun BadgesGrid(
    badges: List<BadgeType>,
    earned: Boolean,
    recognitions: List<Recognition>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        badges.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                row.forEach { badge ->
                    val grantedDate = recognitions.find { it.badge == badge }?.formattedDate()
                    BadgeDetailCard(
                        badge = badge,
                        earned = earned,
                        grantedDate = grantedDate,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BadgeDetailCard(
    badge: BadgeType,
    earned: Boolean,
    grantedDate: String?,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier.alpha(if (earned) 1f else 0.45f),
        containerColor = if (earned && badge.isManual)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surface,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = if (earned) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(Radius.md),
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = badge.icon(),
                        contentDescription = null,
                        tint = if (earned) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = badge.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (earned) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = badge.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
            if (earned && grantedDate != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = grantedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun RecognitionDetailCard(recognition: Recognition) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(Radius.sm),
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = recognition.badge.icon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recognition.badge.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (recognition.note.isNotBlank()) {
                    Text(
                        text = "\"${recognition.note}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Por ${recognition.grantedBy} · ${recognition.formattedDate()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
