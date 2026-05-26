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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.data.projects.ProjectStatus
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.SkeletonListContent
import com.innovagab.app.ui.theme.Error
import com.innovagab.app.ui.theme.Info
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success
import com.innovagab.app.ui.theme.Warning
import java.util.Calendar

// ── Chart color palette (android.graphics.Color) ─────────────────────────────

private val CHART_GRAY = android.graphics.Color.parseColor("#94A3B8")
private val CHART_PENDING = android.graphics.Color.parseColor("#3182CE")
private val CHART_APPROVED = android.graphics.Color.parseColor("#38A169")
private val CHART_IN_PROGRESS = android.graphics.Color.parseColor("#DD6B20")
private val CHART_COMPLETED_IDEA = android.graphics.Color.parseColor("#718096")
private val CHART_REJECTED = android.graphics.Color.parseColor("#E53E3E")
private val CHART_ACTIVE = android.graphics.Color.parseColor("#3182CE")
private val CHART_ON_HOLD = android.graphics.Color.parseColor("#DD6B20")
private val CHART_COMPLETED_PROJECT = android.graphics.Color.parseColor("#38A169")

// ── Value formatter ───────────────────────────────────────────────────────────

private object IntValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String =
        if (value == 0f) "" else value.toInt().toString()
}

// ── Screen entry ──────────────────────────────────────────────────────────────

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        SkeletonListContent(count = 5)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Spacing.xl),
    ) {
        item { PeriodHeader() }
        item { KpiSection(state = state) }
        item { IdeaFunnelSection(state = state) }
        item { ProjectPortfolioSection(state = state) }
        item { InsightSection(state = state) }
    }
}

// ── Period header ─────────────────────────────────────────────────────────────

@Composable
private fun PeriodHeader() {
    val cal = remember { Calendar.getInstance() }
    val quarter = remember { (cal.get(Calendar.MONTH) / 3) + 1 }
    val year = remember { cal.get(Calendar.YEAR) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Q$quarter $year · Visão consolidada",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Success),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Tempo real",
                style = MaterialTheme.typography.labelSmall,
                color = Success,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ── KPI section ───────────────────────────────────────────────────────────────

@Composable
private fun KpiSection(state: DashboardUiState) {
    Column(
        modifier = Modifier.padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            DashboardKpiCard(
                label = "ROI Médio",
                value = formatRoi(state.avgRoi),
                icon = Icons.Default.TrendingUp,
                accentColor = if (state.avgRoi > 0) Success else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            DashboardKpiCard(
                label = "Projetos Ativos",
                value = state.activeProjects.toString(),
                icon = Icons.Default.FolderOpen,
                accentColor = Info,
                modifier = Modifier.weight(1f),
            )
            DashboardKpiCard(
                label = "Ideias Aprovadas",
                value = state.approvedIdeas.toString(),
                icon = Icons.Default.Lightbulb,
                accentColor = Warning,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            DashboardKpiCard(
                label = "Investimento",
                value = formatCurrency(state.totalInvestment),
                icon = Icons.Default.Payments,
                accentColor = Primary,
                modifier = Modifier.weight(1f),
            )
            DashboardKpiCard(
                label = "Economia Gerada",
                value = formatCurrency(state.estimatedSavings),
                icon = Icons.Default.Savings,
                accentColor = Success,
                modifier = Modifier.weight(1f),
            )
            DashboardKpiCard(
                label = "Progresso Médio",
                value = "${(state.avgProgress * 100).toInt()}%",
                icon = Icons.Default.Speed,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DashboardKpiCard(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(Radius.sm))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp),
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
    }
}

// ── Idea funnel bar chart ─────────────────────────────────────────────────────

@Composable
private fun IdeaFunnelSection(state: DashboardUiState) {
    Column(
        modifier = Modifier.padding(
            start = Spacing.md,
            end = Spacing.md,
            top = Spacing.md,
        ),
    ) {
        ChartSectionHeader(
            title = "Funil de Inovação",
            subtitle = "${state.totalIdeas} ideias no total",
        )
        Spacer(Modifier.height(Spacing.sm))
        AppCard {
            if (state.totalIdeas == 0) {
                EmptyChartPlaceholder(message = "Nenhuma ideia registrada ainda.")
            } else {
                IdeaFunnelChart(statusCounts = state.ideaStatusCounts)
            }
        }
    }
}

@Composable
private fun IdeaFunnelChart(statusCounts: Map<IdeaStatus, Int>) {
    val labels = remember { listOf("Análise", "Aprovada", "Execução", "Concluída", "Rejeitada") }
    val barColors = remember {
        listOf(CHART_PENDING, CHART_APPROVED, CHART_IN_PROGRESS, CHART_COMPLETED_IDEA, CHART_REJECTED)
    }
    val entries = remember(statusCounts) {
        IdeaStatus.entries.mapIndexed { index, status ->
            BarEntry(index.toFloat(), statusCounts[status]?.toFloat() ?: 0f)
        }
    }

    AndroidView(
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                setTouchEnabled(false)
                legend.isEnabled = false

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(labels)
                    textSize = 9.5f
                    textColor = CHART_GRAY
                    labelRotationAngle = -15f
                    yOffset = 4f
                }
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    axisMinimum = 0f
                    granularity = 1f
                    textColor = CHART_GRAY
                    textSize = 9.5f
                    setLabelCount(5, false)
                }
                axisRight.isEnabled = false
                setExtraOffsets(4f, 8f, 4f, 16f)
            }
        },
        update = { chart ->
            val dataSet = BarDataSet(entries, "").apply {
                colors = barColors
                valueFormatter = IntValueFormatter
                valueTextSize = 10f
                valueTextColor = CHART_GRAY
            }
            val shouldAnimate = chart.data == null
            chart.data = BarData(dataSet).apply { barWidth = 0.58f }
            if (shouldAnimate) chart.animateY(700)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
    )
}

// ── Project portfolio donut chart ─────────────────────────────────────────────

@Composable
private fun ProjectPortfolioSection(state: DashboardUiState) {
    Column(
        modifier = Modifier.padding(
            start = Spacing.md,
            end = Spacing.md,
            top = Spacing.md,
        ),
    ) {
        ChartSectionHeader(
            title = "Portfólio de Projetos",
            subtitle = "${state.totalProjects} projetos",
        )
        Spacer(Modifier.height(Spacing.sm))
        AppCard {
            if (state.totalProjects == 0) {
                EmptyChartPlaceholder(message = "Nenhum projeto no portfólio ainda.")
            } else {
                ProjectPieChart(statusCounts = state.projectStatusCounts)
            }
        }
    }
}

@Composable
private fun ProjectPieChart(statusCounts: Map<ProjectStatus, Int>) {
    data class Slice(val label: String, val count: Int, val color: Int)

    val slices = remember(statusCounts) {
        listOf(
            Slice("Em andamento", statusCounts[ProjectStatus.ACTIVE] ?: 0, CHART_ACTIVE),
            Slice("Em pausa", statusCounts[ProjectStatus.ON_HOLD] ?: 0, CHART_ON_HOLD),
            Slice("Concluído", statusCounts[ProjectStatus.COMPLETED] ?: 0, CHART_COMPLETED_PROJECT),
        ).filter { it.count > 0 }
    }

    if (slices.isEmpty()) {
        EmptyChartPlaceholder(message = "Sem dados de status disponíveis.")
        return
    }

    val pieEntries = remember(slices) { slices.map { PieEntry(it.count.toFloat(), it.label) } }
    val pieColors = remember(slices) { slices.map { it.color } }

    AndroidView(
        factory = { ctx ->
            PieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                isDrawHoleEnabled = true
                holeRadius = 55f
                transparentCircleRadius = 60f
                setTransparentCircleColor(android.graphics.Color.WHITE)
                setHoleColor(android.graphics.Color.WHITE)
                setDrawEntryLabels(false)
                setTouchEnabled(false)

                legend.apply {
                    isEnabled = true
                    form = Legend.LegendForm.CIRCLE
                    formSize = 10f
                    formToTextSpace = 6f
                    textSize = 11f
                    textColor = CHART_GRAY
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    xEntrySpace = 20f
                    yOffset = 8f
                }
                setExtraOffsets(8f, 8f, 8f, 16f)
            }
        },
        update = { chart ->
            val dataSet = PieDataSet(pieEntries, "").apply {
                colors = pieColors
                sliceSpace = 3f
                selectionShift = 0f
                setDrawValues(true)
                valueTextSize = 11f
                valueTextColor = android.graphics.Color.WHITE
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String =
                        if (value < 5f) "" else "%.0f%%".format(value)
                }
            }
            val shouldAnimate = chart.data == null
            chart.data = PieData(dataSet)
            if (shouldAnimate) chart.animateY(700)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
    )
}

// ── Insight card ──────────────────────────────────────────────────────────────

@Composable
private fun InsightSection(state: DashboardUiState) {
    val text = when {
        state.isLoading -> return
        state.pendingIdeas > 0 ->
            "${state.pendingIdeas} ${if (state.pendingIdeas == 1) "ideia aguarda" else "ideias aguardam"} análise no pipeline de inovação."
        state.approvedIdeas > 0 ->
            "${state.approvedIdeas} ${if (state.approvedIdeas == 1) "ideia foi convertida" else "ideias foram convertidas"} em projetos ativos."
        state.totalIdeas == 0 ->
            "Nenhuma ideia registrada. Incentive os operadores a participar."
        else -> return
    }

    AppCard(
        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.md),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(Spacing.sm))
            Column {
                Text(
                    text = "Destaque executivo",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

// ── Shared subcomponents ──────────────────────────────────────────────────────

@Composable
private fun ChartSectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyChartPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Formatting helpers ────────────────────────────────────────────────────────

private fun formatRoi(roi: Double): String {
    if (roi == 0.0) return "—"
    return "%s%.1f%%".format(if (roi > 0) "+" else "", roi)
}

private fun formatCurrency(amount: Double): String {
    if (amount <= 0.0) return "—"
    return when {
        amount >= 1_000_000 -> "R$ %.1fM".format(amount / 1_000_000)
        amount >= 1_000 -> "R$ %.0fk".format(amount / 1_000)
        else -> "R$ %.0f".format(amount)
    }
}
