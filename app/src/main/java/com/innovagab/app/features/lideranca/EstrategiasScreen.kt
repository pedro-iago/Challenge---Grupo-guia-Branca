package com.innovagab.app.features.lideranca

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryCard
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success

private data class Pilar(
    val titulo: String,
    val descricao: String,
    val icon: ImageVector,
    val cor: Color,
)

private data class KeyResult(
    val descricao: String,
    val progresso: Float,
    val meta: String,
)

private data class ObjetivoItem(
    val objetivo: String,
    val keyResults: List<KeyResult>,
)

private val pilares = listOf(
    Pilar("Crescimento", "Dobrar receita recorrente até o final do ano.", Icons.Default.TrendingUp, Color(0xFF1D3F8E)),
    Pilar("Inovação", "Lançar 3 novos produtos orientados por dados.", Icons.Default.Lightbulb, Color(0xFFDD6B20)),
    Pilar("Pessoas", "Atingir 90% de engajamento e retenção da equipe.", Icons.Default.Groups, Success),
    Pilar("Sustentabilidade", "Neutralizar emissões de carbono nas operações.", Icons.Default.Public, Color(0xFF38A169)),
)

private val objetivos = listOf(
    ObjetivoItem(
        objetivo = "Escalar operações com eficiência",
        keyResults = listOf(
            KeyResult("Reduzir custo por cliente em 20%", 0.68f, "R$ 320 → R$ 256"),
            KeyResult("Aumentar NPS para 80+", 0.74f, "74 atual"),
            KeyResult("Automatizar 50% dos processos manuais", 0.40f, "20 de 40 processos"),
        ),
    ),
    ObjetivoItem(
        objetivo = "Fortalecer presença de mercado",
        keyResults = listOf(
            KeyResult("Expandir para 3 novas regiões", 0.33f, "1 de 3 regiões"),
            KeyResult("Aumentar share de voz em 15%", 0.55f, "8% de 15%"),
        ),
    ),
)

@Composable
fun EstrategiasScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item { CicloBanner() }
        item { PilaresSection() }
        item { OkrSection() }
    }
}

@Composable
private fun CicloBanner() {
    AppPrimaryCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.padding(horizontal = Spacing.sm))
            Column {
                Text(
                    text = "Ciclo Estratégico 2025",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Text(
                    text = "Foco: Crescimento Sustentável",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(Spacing.sm))
        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Q2 2025 · Revisão em Jul/2025",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun PilaresSection() {
    Column {
        Text(
            text = "Pilares estratégicos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
        pilares.forEach { pilar ->
            PilarCard(pilar = pilar)
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

@Composable
private fun PilarCard(pilar: Pilar) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = pilar.cor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = pilar.icon,
                        contentDescription = null,
                        tint = pilar.cor,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = Spacing.md)
                    .weight(1f),
            ) {
                Text(
                    text = pilar.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = pilar.cor,
                )
                Text(
                    text = pilar.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OkrSection() {
    Column {
        Text(
            text = "Objetivos e resultados-chave",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
        objetivos.forEach { obj ->
            AppCard {
                Text(
                    text = obj.objetivo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(Spacing.sm))
                obj.keyResults.forEachIndexed { index, kr ->
                    KrRow(kr = kr)
                    if (index < obj.keyResults.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.sm),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

@Composable
private fun KrRow(kr: KeyResult) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = kr.descricao,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f).padding(end = Spacing.sm),
            )
            Text(
                text = "${(kr.progresso * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { kr.progresso },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = kr.meta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
