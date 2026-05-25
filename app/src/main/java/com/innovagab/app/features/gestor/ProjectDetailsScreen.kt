package com.innovagab.app.features.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.projects.Project
import com.innovagab.app.data.projects.ProjectStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryButton
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success
import com.innovagab.app.ui.theme.Warning

@Composable
fun ProjectDetailsScreen(
    projectId: String,
    viewModel: ProjectViewModel,
    onEditClick: () -> Unit,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(projectId) {
        viewModel.loadProjectDetail(projectId)
    }

    when {
        state.isLoading -> {
            SkeletonListContent(count = 3)
        }

        state.project == null -> {
            EmptyState(
                icon = Icons.Default.FolderOpen,
                title = "Projeto não encontrado",
                subtitle = "Este projeto pode ter sido removido.",
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            ProjectDetailsContent(
                project = state.project!!,
                onEditClick = onEditClick,
            )
        }
    }
}

@Composable
private fun ProjectDetailsContent(
    project: Project,
    onEditClick: () -> Unit,
) {
    val progressColor = project.progressColor()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Spacing.xl),
    ) {
        item { ProjectHeaderBanner(project = project, progressColor = progressColor) }

        item {
            Column(modifier = Modifier.padding(Spacing.md)) {
                KpiGrid(project = project, progressColor = progressColor)
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                ProjectProgressCard(project = project, progressColor = progressColor)
                Spacer(Modifier.height(Spacing.md))
                ProjectTimelineCard(project = project)
                Spacer(Modifier.height(Spacing.md))
                AppPrimaryButton(
                    text = "Editar Indicadores",
                    onClick = onEditClick,
                )
            }
        }
    }
}

// ── Header banner ─────────────────────────────────────────────────────────────

@Composable
private fun ProjectHeaderBanner(project: Project, progressColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.md),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = project.title.ifBlank { "Projeto" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Spacing.sm),
                )
                ProjectStatusChip(status = project.status)
            }
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Criado em ${project.formattedDate()} · ${project.daysSinceCreation()} dias em andamento",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
            )
            Spacer(Modifier.height(Spacing.md))
            LinearProgressIndicator(
                progress = { project.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(Radius.full)),
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f),
                strokeCap = StrokeCap.Round,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${project.progressPercent()}% concluído",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
            )
        }
    }
}

// ── KPI grid ──────────────────────────────────────────────────────────────────

@Composable
private fun KpiGrid(project: Project, progressColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        KpiCard(
            label = "Progresso",
            value = "${project.progressPercent()}%",
            icon = Icons.Default.Assessment,
            color = progressColor,
            modifier = Modifier.weight(1f),
        )
        KpiCard(
            label = "ROI",
            value = project.formattedRoi(),
            icon = Icons.Default.TrendingUp,
            color = Success,
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(Spacing.sm))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        KpiCard(
            label = "Investimento",
            value = project.formattedInvestment(),
            icon = Icons.Default.Payments,
            color = Primary,
            modifier = Modifier.weight(1f),
        )
        KpiCard(
            label = "Prazo",
            value = project.deadline.ifBlank { "—" },
            icon = Icons.Default.CalendarToday,
            color = if (project.deadline.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
            else Warning,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Progress card ─────────────────────────────────────────────────────────────

@Composable
private fun ProjectProgressCard(project: Project, progressColor: Color) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Progresso do Projeto",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "${project.progressPercent()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = progressColor,
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        LinearProgressIndicator(
            progress = { project.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(Radius.full)),
            color = progressColor,
            trackColor = progressColor.copy(alpha = 0.12f),
            strokeCap = StrokeCap.Round,
        )
        Spacer(Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "0%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val days = project.daysSinceCreation()
            Text(
                text = "Iniciado há $days ${if (days == 1) "dia" else "dias"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "100%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Timeline card ─────────────────────────────────────────────────────────────

@Composable
private fun ProjectTimelineCard(project: Project) {
    AppCard {
        Text(
            text = "Linha do Tempo",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(Spacing.md))

        val hasDeadline = project.deadline.isNotBlank()
        val statusColor = project.status.statusColor

        TimelineItem(
            dotColor = Success,
            filled = true,
            title = "Projeto criado",
            value = project.formattedDate(),
            showConnector = true,
        )
        TimelineItem(
            dotColor = statusColor,
            filled = true,
            title = "Status atual",
            value = project.status.label,
            showConnector = hasDeadline,
        )
        if (hasDeadline) {
            TimelineItem(
                dotColor = Warning,
                filled = false,
                title = "Prazo estimado",
                value = project.deadline,
                showConnector = false,
            )
        }
    }
}

@Composable
private fun TimelineItem(
    dotColor: Color,
    filled: Boolean,
    title: String,
    value: String,
    showConnector: Boolean,
) {
    Row {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .then(
                        if (filled) Modifier.background(dotColor)
                        else Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .border(2.dp, dotColor, CircleShape)
                    ),
            )
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }
        Spacer(Modifier.width(Spacing.sm))
        Column(
            modifier = Modifier.padding(
                bottom = if (showConnector) Spacing.xs else 0.dp,
            ),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
