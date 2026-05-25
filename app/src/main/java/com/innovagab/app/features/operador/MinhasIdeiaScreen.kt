package com.innovagab.app.features.operador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.ideas.Idea
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.IdeaCard
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.components.color
import com.innovagab.app.ui.theme.Spacing

private data class FilterOption(val status: IdeaStatus?, val label: String)

private val filterOptions = listOf(FilterOption(null, "Todas")) +
    IdeaStatus.entries.map { FilterOption(it, it.label) }

@Composable
fun MyIdeasScreen(
    viewModel: IdeaViewModel,
    onIdeaClick: (String) -> Unit,
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var activeFilter by remember { mutableStateOf<IdeaStatus?>(null) }

    val filtered = if (activeFilter == null) state.ideas
    else state.ideas.filter { it.status == activeFilter }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(filterOptions) { option ->
                FilterChip(
                    selected = activeFilter == option.status,
                    onClick = { activeFilter = option.status },
                    label = { Text(option.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = option.status?.color?.copy(alpha = 0.12f)
                            ?: MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = option.status?.color
                            ?: MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }

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
                    title = if (activeFilter == null) "Nenhuma ideia ainda"
                    else "Sem ideias com este status",
                    subtitle = if (activeFilter == null) "Suas ideias enviadas aparecerão aqui."
                    else "Tente um filtro diferente.",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            else -> {
                IdeasList(
                    ideas = filtered,
                    totalCount = state.ideas.size,
                    onIdeaClick = onIdeaClick,
                )
            }
        }
    }
}

@Composable
private fun IdeasList(
    ideas: List<Idea>,
    totalCount: Int,
    onIdeaClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Spacing.md,
            end = Spacing.md,
            bottom = Spacing.md,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            Text(
                text = "${ideas.size} de $totalCount ${if (totalCount == 1) "ideia" else "ideias"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.xs),
            )
        }
        items(ideas, key = { it.id }) { idea ->
            IdeaCard(
                idea = idea,
                onClick = { onIdeaClick(idea.id) },
            )
        }
    }
}
