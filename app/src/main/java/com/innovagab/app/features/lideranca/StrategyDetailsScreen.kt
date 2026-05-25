package com.innovagab.app.features.lideranca

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.strategies.Strategy
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@Composable
fun StrategyDetailsScreen(
    strategyId: String,
    viewModel: StrategiesViewModel,
    canEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleted: () -> Unit,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(strategyId) {
        viewModel.loadStrategyDetail(strategyId)
    }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            viewModel.resetDetailState()
            onDeleted()
        }
    }

    when {
        state.isLoading -> {
            SkeletonListContent(count = 3)
        }

        state.strategy == null -> {
            EmptyState(
                icon = Icons.Default.Inbox,
                title = "Estratégia não encontrada",
                subtitle = "Não foi possível carregar os dados.",
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            StrategyDetailsContent(
                strategy = state.strategy!!,
                canEdit = canEdit,
                isDeleting = state.isDeleting,
                error = state.error,
                onEditClick = onEditClick,
                onDeleteClick = { showDeleteDialog = true },
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Remover estratégia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            text = {
                Text(
                    text = "Essa ação não pode ser desfeita. A estratégia será removida permanentemente.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteStrategy(strategyId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun StrategyDetailsContent(
    strategy: Strategy,
    canEdit: Boolean,
    isDeleting: Boolean,
    error: String?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val accentColor = strategy.pillar.color()
    val pillarIcon = strategy.pillar.icon()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            AppPrimaryCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Icon(
                                    imageVector = pillarIcon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp).size(16.dp),
                                )
                            }
                            Spacer(Modifier.width(Spacing.sm))
                            Text(
                                text = strategy.pillar.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                        }
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = strategy.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                    StatusBadge(status = strategy.status)
                }
                Spacer(Modifier.height(Spacing.sm))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (strategy.cycle.isNotBlank()) {
                        HeaderMeta(label = "Ciclo", value = strategy.cycle)
                    }
                    if (strategy.deadline.isNotBlank()) {
                        HeaderMeta(label = "Prazo", value = strategy.deadline)
                    }
                    HeaderMeta(label = "Prioridade", value = strategy.priority.label)
                }
            }
        }

        if (strategy.description.isNotBlank()) {
            item {
                AppCard {
                    DetailSectionLabel("Descrição")
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = strategy.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        item {
            AppCard {
                DetailSectionLabel("Detalhes executivos")
                Spacer(Modifier.height(Spacing.sm))
                DetailRow(label = "Pilar estratégico", value = strategy.pillar.label)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.sm),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                DetailRow(label = "Prioridade", value = strategy.priority.label)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.sm),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                DetailRow(label = "Status", value = strategy.status.label)
                if (strategy.owner.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    DetailRow(label = "Responsável", value = strategy.owner)
                }
                if (strategy.cycle.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    DetailRow(label = "Ciclo", value = strategy.cycle)
                }
                if (strategy.deadline.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    DetailRow(label = "Prazo", value = strategy.deadline)
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Spacing.sm),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                DetailRow(label = "Criado em", value = strategy.formattedDate())
            }
        }

        if (error != null) {
            item {
                AppCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        if (canEdit) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    OutlinedButton(
                        onClick = onDeleteClick,
                        enabled = !isDeleting,
                        shape = RoundedCornerShape(Radius.md),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.error,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp),
                            )
                        } else {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(Spacing.xs))
                            Text("Remover", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Button(
                        onClick = onEditClick,
                        enabled = !isDeleting,
                        shape = RoundedCornerShape(Radius.md),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Spacing.xs))
                        Text("Editar", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(Spacing.md))
            }
        }
    }
}

@Composable
private fun HeaderMeta(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

@Composable
private fun DetailSectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
