package com.innovagab.app.features.recognition

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.gamification.BadgeType
import com.innovagab.app.data.gamification.Recognition
import com.innovagab.app.data.gamification.UserScore
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppSurfaceCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@Composable
fun LeaderboardScreen(
    viewModel: RecognitionViewModel,
    currentUserId: String?,
    canGrant: Boolean,
    onUserClick: (String) -> Unit,
    onGrantClick: () -> Unit,
) {
    val state by viewModel.leaderboardState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            if (canGrant) {
                FloatingActionButton(
                    onClick = onGrantClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Conceder Reconhecimento")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Ranking",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Reconhecimentos",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                )
            }

            when {
                state.isLoading -> {
                    SkeletonListContent()
                }

                selectedTab == 0 -> {
                    RankingTab(
                        scores = state.scores,
                        currentUserId = currentUserId,
                        onUserClick = onUserClick,
                    )
                }

                else -> {
                    RecognitionsTab(recognitions = state.recentRecognitions)
                }
            }
        }
    }
}

@Composable
private fun RankingTab(
    scores: List<UserScore>,
    currentUserId: String?,
    onUserClick: (String) -> Unit,
) {
    if (scores.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Groups,
            title = "Nenhum colaborador ainda",
            subtitle = "Os rankings serão exibidos conforme as contribuições forem feitas.",
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (scores.size >= 3) {
            item { PodiumSection(scores = scores.take(3), currentUserId = currentUserId, onUserClick = onUserClick) }
            item { Spacer(Modifier.height(Spacing.xs)) }
            if (scores.size > 3) {
                item {
                    Text(
                        text = "Classificação geral",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = Spacing.xs),
                    )
                }
                itemsIndexed(scores.drop(3)) { index, score ->
                    RankingRow(
                        rank = index + 4,
                        score = score,
                        isCurrentUser = score.userId == currentUserId,
                        onClick = { onUserClick(score.userId) },
                    )
                }
            }
        } else {
            itemsIndexed(scores) { index, score ->
                RankingRow(
                    rank = index + 1,
                    score = score,
                    isCurrentUser = score.userId == currentUserId,
                    onClick = { onUserClick(score.userId) },
                )
            }
        }
        item { Spacer(Modifier.height(72.dp)) }
    }
}

@Composable
private fun PodiumSection(
    scores: List<UserScore>,
    currentUserId: String?,
    onUserClick: (String) -> Unit,
) {
    AppCard {
        Text(
            text = "Destaques",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = Spacing.md),
        )
        scores.forEachIndexed { index, score ->
            PodiumRow(
                rank = index + 1,
                score = score,
                isCurrentUser = score.userId == currentUserId,
                onClick = { onUserClick(score.userId) },
            )
            if (index < scores.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.sm),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun PodiumRow(
    rank: Int,
    score: UserScore,
    isCurrentUser: Boolean,
    onClick: () -> Unit,
) {
    val (rankColor, rankLabel) = when (rank) {
        1 -> Primary to "1º"
        2 -> Color(0xFF4A5568) to "2º"
        else -> Color(0xFF718096) to "3º"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = rankColor.copy(alpha = if (rank == 1) 1f else 0.08f),
            shape = RoundedCornerShape(Radius.sm),
            modifier = Modifier.size(32.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = rankLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) Color.White else rankColor,
                )
            }
        }
        Spacer(Modifier.width(Spacing.md))
        AvatarCircle(initials = score.initials, color = rankColor, size = 36)
        Spacer(Modifier.width(Spacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = score.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                if (isCurrentUser) {
                    Spacer(Modifier.width(Spacing.xs))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(Radius.full),
                    ) {
                        Text(
                            text = "Você",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
            if (score.allBadges.isNotEmpty()) {
                Text(
                    text = score.allBadges.first().title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${score.points}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = rankColor,
            )
            Text(
                text = "pontos",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RankingRow(
    rank: Int,
    score: UserScore,
    isCurrentUser: Boolean,
    onClick: () -> Unit,
) {
    AppCard(
        onClick = onClick,
        containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surface,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%02d".format(rank),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(28.dp),
            )
            Spacer(Modifier.width(Spacing.sm))
            AvatarCircle(
                initials = score.initials,
                color = MaterialTheme.colorScheme.primary,
                size = 32,
            )
            Spacer(Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = score.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrentUser) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                if (score.allBadges.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(score.allBadges.take(3).toList()) { badge ->
                            BadgeChip(badge = badge)
                        }
                        if (score.allBadges.size > 3) {
                            item {
                                BadgeOverflowChip(count = score.allBadges.size - 3)
                            }
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${score.points}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RecognitionsTab(recognitions: List<Recognition>) {
    if (recognitions.isEmpty()) {
        EmptyState(
            icon = Icons.Default.EmojiEvents,
            title = "Nenhum reconhecimento ainda",
            subtitle = "Os reconhecimentos concedidos pela liderança aparecerão aqui.",
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(recognitions, key = { it.id }) { recognition ->
            RecognitionFeedCard(recognition = recognition)
        }
    }
}

@Composable
private fun RecognitionFeedCard(recognition: Recognition) {
    AppCard {
        Row(verticalAlignment = Alignment.Top) {
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
                    text = recognition.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = recognition.badge.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (recognition.note.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "\"${recognition.note}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Por ${recognition.grantedBy} · ${recognition.formattedDate()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun AvatarCircle(initials: String, color: Color, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials.take(2),
            style = if (size >= 40) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

@Composable
internal fun BadgeChip(badge: BadgeType, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(Radius.full),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                imageVector = badge.icon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(10.dp),
            )
            Text(
                text = badge.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BadgeOverflowChip(count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(Radius.full),
    ) {
        Text(
            text = "+$count",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
