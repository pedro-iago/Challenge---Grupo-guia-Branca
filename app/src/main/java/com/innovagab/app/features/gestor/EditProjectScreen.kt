package com.innovagab.app.features.gestor

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.projects.Project
import com.innovagab.app.data.projects.ProjectStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.ModernTextField
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@Composable
fun EditProjectScreen(
    projectId: String,
    viewModel: ProjectViewModel,
    onSaveSuccess: () -> Unit,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(projectId) {
        viewModel.loadProjectDetail(projectId)
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveState()
            onSaveSuccess()
        }
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        state.project == null -> {
            EmptyState(
                icon = Icons.Default.Inbox,
                title = "Projeto não encontrado",
                subtitle = "Não foi possível carregar os dados.",
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            EditProjectContent(
                project = state.project!!,
                isSaving = state.isSaving,
                error = state.error,
                onSave = { progress, roi, investment, deadline, status ->
                    viewModel.saveProjectEdit(projectId, progress, roi, investment, deadline, status)
                },
            )
        }
    }
}

@Composable
private fun EditProjectContent(
    project: Project,
    isSaving: Boolean,
    error: String?,
    onSave: (Float, Double, Double, String, ProjectStatus) -> Unit,
) {
    var progressValue by remember(project.id) { mutableFloatStateOf(project.progress) }
    var roiValue by remember(project.id) {
        mutableStateOf(if (project.roi == 0.0) "" else "%.1f".format(project.roi))
    }
    var investmentValue by remember(project.id) {
        mutableStateOf(if (project.investment == 0.0) "" else "%.0f".format(project.investment))
    }
    var deadlineValue by remember(project.id) { mutableStateOf(project.deadline) }
    var statusValue by remember(project.id) { mutableStateOf(project.status) }

    val progressColor = project.copy(progress = progressValue).progressColor()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Progress section
        item {
            AppCard {
                EditSectionLabel(label = "Progresso do projeto")
                Spacer(Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "0%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${(progressValue * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = progressColor,
                    )
                    Text(
                        text = "100%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Slider(
                    value = progressValue,
                    onValueChange = { progressValue = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = progressColor,
                        activeTrackColor = progressColor,
                        inactiveTrackColor = progressColor.copy(alpha = 0.2f),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Financial section
        item {
            AppCard {
                EditSectionLabel(label = "Indicadores financeiros")
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = roiValue,
                    onValueChange = { roiValue = it },
                    label = "ROI (%)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    supportingText = "Ex: 15.5 para 15,5% de retorno",
                )
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = investmentValue,
                    onValueChange = { investmentValue = it },
                    label = "Investimento (R$)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    supportingText = "Valor total investido no projeto",
                )
            }
        }

        // Timeline section
        item {
            AppCard {
                EditSectionLabel(label = "Cronograma")
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = deadlineValue,
                    onValueChange = { deadlineValue = it },
                    label = "Prazo estimado",
                    supportingText = "Formato: dd/MM/yyyy",
                )
            }
        }

        // Status section
        item {
            AppCard {
                EditSectionLabel(label = "Status do projeto")
                Spacer(Modifier.height(Spacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ProjectStatus.entries.forEach { status ->
                        val statusColor = status.statusColor
                        FilterChip(
                            selected = statusValue == status,
                            onClick = { statusValue = status },
                            label = {
                                Text(
                                    text = status.label,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = statusColor.copy(alpha = 0.15f),
                                selectedLabelColor = statusColor,
                            ),
                        )
                    }
                }
            }
        }

        // Error
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

        // Save button
        item {
            Button(
                onClick = {
                    val roi = roiValue.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val investment = investmentValue.replace(",", ".").toDoubleOrNull() ?: 0.0
                    onSave(progressValue, roi, investment, deadlineValue.trim(), statusValue)
                },
                enabled = !isSaving,
                shape = RoundedCornerShape(Radius.md),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text("Salvar Alterações", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun EditSectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )
}
