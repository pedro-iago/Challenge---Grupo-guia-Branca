package com.innovagab.app.features.operador

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.ideas.Idea
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.StatusChip
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@Composable
fun IdeaDetailsScreen(
    ideaId: String,
    viewModel: IdeaViewModel,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(ideaId) {
        viewModel.loadIdeaDetail(ideaId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        state.idea == null -> {
            EmptyState(
                icon = Icons.Default.Inbox,
                title = "Ideia não encontrada",
                subtitle = "Esta ideia pode ter sido removida.",
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            IdeaDetailContent(
                idea = state.idea!!,
                isUpdating = state.isUpdating,
                onWithdraw = { viewModel.updateStatus(ideaId, IdeaStatus.COMPLETED) },
            )
        }
    }
}

@Composable
private fun IdeaDetailContent(
    idea: Idea,
    isUpdating: Boolean,
    onWithdraw: () -> Unit,
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Cancelar ideia") },
            text = { Text("Deseja retirar esta ideia da análise? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = { onWithdraw(); showWithdrawDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text("Retirar") }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) { Text("Cancelar") }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item { IdeaHeaderSection(idea = idea) }
        item { IdeaMetadataCard(idea = idea) }
        item { IdeaDescriptionCard(idea = idea) }
        if (idea.status == IdeaStatus.PENDING) {
            item {
                OutlinedButton(
                    onClick = { showWithdrawDialog = true },
                    enabled = !isUpdating,
                    shape = RoundedCornerShape(Radius.md),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        Text("Retirar ideia", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(Spacing.md))
            }
        }
    }
}

@Composable
private fun IdeaHeaderSection(idea: Idea) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = idea.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Spacing.sm),
            )
            StatusChip(status = idea.status)
        }
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "Enviada em ${idea.formattedDate()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun IdeaMetadataCard(idea: Idea) {
    AppCard {
        MetaRow(label = "Categoria", value = idea.category)
        HorizontalDivider(
            modifier = Modifier.padding(vertical = Spacing.sm),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        MetaRow(label = "Impacto", value = idea.impact)
        HorizontalDivider(
            modifier = Modifier.padding(vertical = Spacing.sm),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        MetaRow(label = "Prioridade", value = idea.priority.label)
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun IdeaDescriptionCard(idea: Idea) {
    AppCard {
        Text(
            text = "Descrição",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = idea.description.ifBlank { "Sem descrição." },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
        )
    }
}
