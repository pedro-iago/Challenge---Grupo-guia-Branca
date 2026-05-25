package com.innovagab.app.features.lideranca

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.strategies.Strategy
import com.innovagab.app.data.strategies.StrategyPillar
import com.innovagab.app.data.strategies.StrategyPriority
import com.innovagab.app.data.strategies.StrategyStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.EmptyState
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success
import com.innovagab.app.ui.theme.Warning

private val pillarColor = mapOf(
    StrategyPillar.GROWTH to Color(0xFF1D3F8E),
    StrategyPillar.INNOVATION to Color(0xFFDD6B20),
    StrategyPillar.PEOPLE to Color(0xFF38A169),
    StrategyPillar.SUSTAINABILITY to Color(0xFF2D7D4E),
    StrategyPillar.EFFICIENCY to Color(0xFF805AD5),
    StrategyPillar.CUSTOMER to Color(0xFF3182CE),
)

private val pillarIcon = mapOf(
    StrategyPillar.GROWTH to Icons.Default.TrendingUp,
    StrategyPillar.INNOVATION to Icons.Default.Lightbulb,
    StrategyPillar.PEOPLE to Icons.Default.Groups,
    StrategyPillar.SUSTAINABILITY to Icons.Default.Recycling,
    StrategyPillar.EFFICIENCY to Icons.Default.Equalizer,
    StrategyPillar.CUSTOMER to Icons.Default.Explore,
)

internal fun StrategyPillar.color(): Color = pillarColor[this] ?: Color(0xFF1D3F8E)
internal fun StrategyPillar.icon(): ImageVector = pillarIcon[this] ?: Icons.Default.EmojiObjects

internal fun StrategyStatus.color(): Color = when (this) {
    StrategyStatus.ACTIVE -> Success
    StrategyStatus.DRAFT -> Warning
    StrategyStatus.ARCHIVED -> Color(0xFF718096)
}

internal fun StrategyPriority.color(): Color = when (this) {
    StrategyPriority.HIGH -> Color(0xFFE53E3E)
    StrategyPriority.MEDIUM -> Warning
    StrategyPriority.LOW -> Success
}

@Composable
fun StrategiesScreen(
    viewModel: StrategiesViewModel,
    canEdit: Boolean,
    onStrategyClick: (String) -> Unit,
    onCreateClick: () -> Unit = {},
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (canEdit) {
                FloatingActionButton(
                    onClick = onCreateClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Estratégia")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        when {
            state.isLoading -> {
                SkeletonListContent(modifier = Modifier.padding(innerPadding))
            }

            state.strategies.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.EmojiObjects,
                    title = "Nenhuma estratégia cadastrada",
                    subtitle = if (canEdit) "Crie a primeira estratégia corporativa." else "Aguarde a liderança publicar estratégias.",
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    item { SummaryHeader(strategies = state.strategies) }
                    items(state.strategies, key = { it.id }) { strategy ->
                        StrategyCard(
                            strategy = strategy,
                            onClick = { onStrategyClick(strategy.id) },
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(strategies: List<Strategy>) {
    val active = strategies.count { it.status == StrategyStatus.ACTIVE }
    val draft = strategies.count { it.status == StrategyStatus.DRAFT }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        SummaryChip(
            label = "Total",
            value = "${strategies.size}",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        SummaryChip(
            label = "Ativas",
            value = "$active",
            color = Success,
            modifier = Modifier.weight(1f),
        )
        SummaryChip(
            label = "Rascunho",
            value = "$draft",
            color = Warning,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(Radius.md),
        color = color.copy(alpha = 0.08f),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = Spacing.sm, horizontal = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
fun StrategyCard(strategy: Strategy, onClick: () -> Unit) {
    val accentColor = strategy.pillar.color()
    val icon = strategy.pillar.icon()

    AppCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(accentColor, RoundedCornerShape(Radius.full)),
            )
            Spacer(Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = accentColor.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(6.dp),
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.padding(4.dp).size(14.dp),
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = strategy.pillar.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    StatusBadge(status = strategy.status)
                }
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = strategy.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (strategy.description.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = strategy.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
                Spacer(Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PriorityBadge(priority = strategy.priority)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        if (strategy.cycle.isNotBlank()) {
                            MetaLabel(label = strategy.cycle)
                        }
                        if (strategy.deadline.isNotBlank()) {
                            MetaLabel(label = "Até ${strategy.deadline}")
                        }
                    }
                }
                if (strategy.owner.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Responsável: ${strategy.owner}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
internal fun StatusBadge(status: StrategyStatus) {
    val color = status.color()
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(Radius.full),
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 3.dp),
        )
    }
}

@Composable
internal fun PriorityBadge(priority: StrategyPriority) {
    val color = priority.color()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, shape = RoundedCornerShape(Radius.full)),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "Prioridade ${priority.label}",
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

@Composable
private fun MetaLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
