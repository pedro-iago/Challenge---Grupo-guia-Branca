package com.innovagab.app.features.gestor

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.projects.Project
import com.innovagab.app.data.projects.ProjectStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.theme.Error
import com.innovagab.app.ui.theme.Info
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success
import com.innovagab.app.ui.theme.Warning

private data class ProjectStatusFilter(val status: ProjectStatus?, val label: String)

private val projectStatusFilters = listOf(ProjectStatusFilter(null, "Todos")) +
    ProjectStatus.entries.map { ProjectStatusFilter(it, it.label) }

val ProjectStatus.statusColor: Color
    get() = when (this) {
        ProjectStatus.ACTIVE -> Info
        ProjectStatus.ON_HOLD -> Warning
        ProjectStatus.COMPLETED -> Success
    }

@Composable
fun ProjectsScreen(
    viewModel: ProjectViewModel,
    onProjectClick: (String) -> Unit,
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf<ProjectStatus?>(null) }

    val filtered = if (activeFilter == null) state.projects
    else state.projects.filter { it.status == activeFilter }

    Column(modifier = Modifier.fillMaxSize()) {
        ProjectsSummaryBanner(state = state)

        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(projectStatusFilters) { filter ->
                FilterChip(
                    selected = activeFilter == filter.status,
                    onClick = { activeFilter = filter.status },
                    label = { Text(filter.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = filter.status?.statusColor?.copy(alpha = 0.12f)
                            ?: MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = filter.status?.statusColor
                            ?: MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            state.error != null -> {
                EmptyState(
                    icon = Icons.Default.Inbox,
                    title = "Erro ao carregar",
                    subtitle = state.error ?: "Tente novamente.",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            filtered.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.FolderOpen,
                    title = if (activeFilter == null) "Nenhum projeto ainda"
                    else "Sem projetos com este status",
                    subtitle = if (activeFilter == null) "Projetos aprovados aparecerão aqui automaticamente."
                    else "Tente um filtro diferente.",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Spacing.md,
                        end = Spacing.md,
                        top = Spacing.xs,
                        bottom = Spacing.md,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    item {
                        Text(
                            text = "${filtered.size} ${if (filtered.size == 1) "projeto" else "projetos"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = Spacing.xs),
                        )
                    }
                    items(filtered, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            onClick = { onProjectClick(project.id) },
                        )
                    }
                }
            }
        }
    }
}

// ── Summary banner ────────────────────────────────────────────────────────────

@Composable
private fun ProjectsSummaryBanner(state: ProjectsListUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Portfólio de Projetos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                )
                Text(
                    text = "${state.projects.size} projetos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${state.activeCount} em andamento · ${state.avgProgress.let { "%.0f".format(it * 100) }}% progresso médio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(Radius.md))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

// ── Project card ──────────────────────────────────────────────────────────────

@Composable
fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
) {
    val progressColor = project.progressColor()

    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = project.title.ifBlank { "Projeto sem título" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Spacing.sm),
            )
            ProjectStatusChip(status = project.status)
        }

        Spacer(Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LinearProgressIndicator(
                progress = { project.progress },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(Radius.full)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = "${project.progressPercent()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = progressColor,
            )
        }

        Spacer(Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ProjectMetric(label = "Investimento", value = project.formattedInvestment())
            ProjectMetric(label = "ROI", value = project.formattedRoi())
            if (project.deadline.isNotBlank()) {
                ProjectMetric(label = "Prazo", value = project.deadline)
            }
        }
    }
}

@Composable
private fun ProjectMetric(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun ProjectStatusChip(status: ProjectStatus) {
    val color = status.statusColor
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(Radius.full),
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

fun Project.progressColor(): Color = when {
    progress >= 0.75f -> Success
    progress >= 0.40f -> Warning
    else -> Error
}
