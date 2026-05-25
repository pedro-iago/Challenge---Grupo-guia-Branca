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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.strategies.Strategy
import com.innovagab.app.data.strategies.StrategyPillar
import com.innovagab.app.data.strategies.StrategyPriority
import com.innovagab.app.data.strategies.StrategyStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryButton
import com.innovagab.app.ui.components.ModernTextField
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Spacing

@Composable
fun CreateStrategyScreen(
    viewModel: StrategiesViewModel,
    existingStrategy: Strategy? = null,
    onSuccess: () -> Unit,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetDetailState()
            onSuccess()
        }
    }

    if (state.isLoading && existingStrategy == null) {
        SkeletonListContent(count = 3)
        return
    }

    StrategyForm(
        initial = existingStrategy,
        isSaving = state.isSaving,
        error = state.error,
        onSubmit = { title, description, pillar, status, priority, cycle, deadline, owner ->
            if (existingStrategy != null) {
                viewModel.updateStrategy(
                    existingStrategy.id, title, description, pillar, status, priority, cycle, deadline, owner
                )
            } else {
                viewModel.createStrategy(title, description, pillar, status, priority, cycle, deadline, owner)
            }
        },
    )
}

@Composable
private fun StrategyForm(
    initial: Strategy?,
    isSaving: Boolean,
    error: String?,
    onSubmit: (String, String, StrategyPillar, StrategyStatus, StrategyPriority, String, String, String) -> Unit,
) {
    var title by remember(initial?.id) { mutableStateOf(initial?.title ?: "") }
    var description by remember(initial?.id) { mutableStateOf(initial?.description ?: "") }
    var pillar by remember(initial?.id) { mutableStateOf(initial?.pillar ?: StrategyPillar.GROWTH) }
    var status by remember(initial?.id) { mutableStateOf(initial?.status ?: StrategyStatus.DRAFT) }
    var priority by remember(initial?.id) { mutableStateOf(initial?.priority ?: StrategyPriority.MEDIUM) }
    var cycle by remember(initial?.id) { mutableStateOf(initial?.cycle ?: "") }
    var deadline by remember(initial?.id) { mutableStateOf(initial?.deadline ?: "") }
    var owner by remember(initial?.id) { mutableStateOf(initial?.owner ?: "") }

    val isTitleValid = title.isNotBlank()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            AppCard {
                FormSectionLabel("Identificação")
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Título da estratégia",
                    isError = title.isBlank() && isSaving,
                    supportingText = "Nome executivo da diretriz corporativa",
                )
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descrição",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 6,
                    supportingText = "Contexto e objetivos da estratégia",
                )
            }
        }

        item {
            AppCard {
                FormSectionLabel("Pilar estratégico")
                Spacer(Modifier.height(Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(StrategyPillar.entries) { p ->
                        val color = p.color()
                        FilterChip(
                            selected = pillar == p,
                            onClick = { pillar = p },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = p.icon(),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(p.label, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.14f),
                                selectedLabelColor = color,
                                selectedLeadingIconColor = color,
                            ),
                        )
                    }
                }
            }
        }

        item {
            AppCard {
                FormSectionLabel("Status e prioridade")
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    StrategyStatus.entries.forEach { s ->
                        val color = s.color()
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.14f),
                                selectedLabelColor = color,
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = "Prioridade",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    StrategyPriority.entries.forEach { p ->
                        val color = p.color()
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.14f),
                                selectedLabelColor = color,
                            ),
                        )
                    }
                }
            }
        }

        item {
            AppCard {
                FormSectionLabel("Planejamento")
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = cycle,
                    onValueChange = { cycle = it },
                    label = "Ciclo estratégico",
                    supportingText = "Ex: 2025-Q2, Anual 2025",
                )
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = "Prazo",
                    supportingText = "Ex: dez/2025, 31/12/2025",
                )
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = "Responsável",
                    supportingText = "Área ou executivo responsável",
                )
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

        item {
            AppPrimaryButton(
                text = if (initial != null) "Salvar Alterações" else "Publicar Estratégia",
                onClick = {
                    onSubmit(title.trim(), description.trim(), pillar, status, priority, cycle.trim(), deadline.trim(), owner.trim())
                },
                enabled = isTitleValid,
                isLoading = isSaving,
            )
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun FormSectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )
}
