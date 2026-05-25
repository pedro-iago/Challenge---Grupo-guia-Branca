package com.innovagab.app.features.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.ideas.Idea
import com.innovagab.app.data.ideas.IdeaPriority
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.ui.components.ApprovalActions
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.components.ModernTextField
import com.innovagab.app.ui.components.PrioritySelector
import com.innovagab.app.ui.components.StatusChip
import com.innovagab.app.ui.components.color
import com.innovagab.app.ui.components.priorityColor
import com.innovagab.app.ui.theme.Error
import com.innovagab.app.ui.theme.Info
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success
import com.innovagab.app.ui.theme.Warning

private data class StatusFilter(val status: IdeaStatus?, val label: String)

private val statusFilters = listOf(StatusFilter(null, "Todas")) +
    IdeaStatus.entries.map { StatusFilter(it, it.label) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PipelineScreen(viewModel: PipelineViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val reviewSheet by viewModel.reviewSheet.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf<IdeaStatus?>(null) }

    val filtered = if (activeFilter == null) state.ideas
    else state.ideas.filter { it.status == activeFilter }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PipelineSummaryRow(state = state)

            PipelineFilterBar(
                activeFilter = activeFilter,
                onFilterChange = { activeFilter = it },
                pendingCount = state.pendingCount,
            )

            when {
                state.isLoading -> {
                    SkeletonListContent()
                }

                state.error != null -> {
                    EmptyState(
                        icon = Icons.Default.Warning,
                        title = "Erro ao carregar",
                        subtitle = state.error ?: "Tente novamente mais tarde.",
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                filtered.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.Inbox,
                        title = if (activeFilter == null) "Nenhuma ideia recebida"
                        else "Sem ideias com este status",
                        subtitle = if (activeFilter == null) "As ideias submetidas pelos operadores aparecerão aqui."
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
                                text = "${filtered.size} ${if (filtered.size == 1) "ideia" else "ideias"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = Spacing.xs),
                            )
                        }
                        items(filtered, key = { it.id }) { idea ->
                            PipelineIdeaCard(
                                idea = idea,
                                onClick = { viewModel.openReview(idea) },
                            )
                        }
                    }
                }
            }
        }

        if (reviewSheet is ReviewSheet.Open) {
            val sheet = reviewSheet as ReviewSheet.Open
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { if (!sheet.isSubmitting) viewModel.dismissReview() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                ReviewSheetContent(
                    sheet = sheet,
                    onPriorityChange = { viewModel.updateSheetPriority(it) },
                    onCommentChange = { viewModel.updateSheetComment(it) },
                    onApprove = { viewModel.approveIdea() },
                    onReject = { viewModel.rejectIdea() },
                    onSavePriority = { viewModel.savePriority() },
                )
            }
        }
    }
}

// ── Summary row ───────────────────────────────────────────────────────────────

@Composable
private fun PipelineSummaryRow(state: PipelineUiState) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            SummaryCard(
                label = "Em análise",
                value = state.pendingCount.toString(),
                color = Info,
                icon = Icons.Default.PendingActions,
            )
        }
        item {
            SummaryCard(
                label = "Aprovadas",
                value = state.approvedCount.toString(),
                color = Success,
                icon = Icons.Default.CheckCircle,
            )
        }
        item {
            SummaryCard(
                label = "Rejeitadas",
                value = state.rejectedCount.toString(),
                color = Error,
                icon = Icons.Default.ThumbDown,
            )
        }
        item {
            SummaryCard(
                label = "Total",
                value = state.ideas.size.toString(),
                color = Primary,
                icon = Icons.Default.Lightbulb,
            )
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
) {
    AppCard(modifier = Modifier.width(120.dp)) {
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
                    style = MaterialTheme.typography.titleMedium,
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

// ── Filter bar ────────────────────────────────────────────────────────────────

@Composable
private fun PipelineFilterBar(
    activeFilter: IdeaStatus?,
    onFilterChange: (IdeaStatus?) -> Unit,
    pendingCount: Int,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(statusFilters) { filter ->
            val isSelected = activeFilter == filter.status
            FilterChip(
                selected = isSelected,
                onClick = { onFilterChange(filter.status) },
                label = { Text(filter.label, style = MaterialTheme.typography.labelSmall) },
                trailingIcon = if (filter.status == IdeaStatus.PENDING && pendingCount > 0) {
                    { Badge { Text(pendingCount.toString()) } }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = filter.status?.color?.copy(alpha = 0.12f)
                        ?: MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = filter.status?.color
                        ?: MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

// ── Pipeline idea card ────────────────────────────────────────────────────────

@Composable
private fun PipelineIdeaCard(
    idea: Idea,
    onClick: () -> Unit,
) {
    AppCard(onClick = onClick) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(idea.priorityColor),
            )
            Spacer(Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = idea.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Spacing.sm),
                    )
                    StatusChip(status = idea.status)
                }

                if (idea.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = idea.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(Spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        CategoryTag(idea.category)
                        ImpactTag(idea.impact)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PriorityDot(priority = idea.priority)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = idea.formattedDate(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTag(category: String) {
    if (category.isBlank()) return
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ImpactTag(impact: String) {
    if (impact.isBlank()) return
    val color = when (impact) {
        "Alto" -> Error
        "Médio" -> Warning
        else -> Success
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = impact,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PriorityDot(priority: IdeaPriority) {
    val color = when (priority) {
        IdeaPriority.HIGH -> Error
        IdeaPriority.MEDIUM -> Warning
        IdeaPriority.LOW -> Success
    }
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(RoundedCornerShape(50))
            .background(color),
    )
}

// ── Review bottom sheet ───────────────────────────────────────────────────────

@Composable
private fun ReviewSheetContent(
    sheet: ReviewSheet.Open,
    onPriorityChange: (IdeaPriority) -> Unit,
    onCommentChange: (String) -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onSavePriority: () -> Unit,
) {
    val idea = sheet.idea
    val isPending = idea.status == IdeaStatus.PENDING

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = Spacing.sm)) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${idea.category} · ${idea.impact} · ${idea.formattedDate()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusChip(status = idea.status)
        }

        if (idea.description.isNotBlank()) {
            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Priority
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = "Prioridade",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            PrioritySelector(
                selected = sheet.selectedPriority,
                onSelect = onPriorityChange,
                enabled = !sheet.isSubmitting,
            )
        }

        // Comment
        ModernTextField(
            value = sheet.comment,
            onValueChange = onCommentChange,
            label = "Comentário para o autor",
            singleLine = false,
            minLines = 2,
            maxLines = 4,
        )

        // Error
        if (sheet.error != null) {
            Text(
                text = sheet.error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        // Actions
        if (isPending) {
            ApprovalActions(
                onApprove = onApprove,
                onReject = onReject,
                isSubmitting = sheet.isSubmitting,
            )
        } else {
            OutlinedButton(
                onClick = onSavePriority,
                enabled = !sheet.isSubmitting,
                shape = RoundedCornerShape(Radius.md),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                if (sheet.isSubmitting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                } else {
                    Text("Salvar alterações", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
