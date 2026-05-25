package com.innovagab.app.features.operador

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innovagab.app.data.ideas.IdeaPriority
import com.innovagab.app.data.ideas.ideaCategories
import com.innovagab.app.data.ideas.ideaImpacts
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryCard
import com.innovagab.app.ui.components.ModernTextField
import com.innovagab.app.ui.theme.Primary
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateIdeaScreen(viewModel: IdeaViewModel) {
    val createState by viewModel.createState.collectAsStateWithLifecycle()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<String?>(null) }
    var impacto by remember { mutableStateOf<String?>(null) }
    var prioridade by remember { mutableStateOf(IdeaPriority.MEDIUM) }
    var submitted by remember { mutableStateOf(false) }
    var tituloError by remember { mutableStateOf<String?>(null) }
    var descricaoError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(createState) {
        if (createState is IdeaCreateUiState.Success) {
            submitted = true
            titulo = ""
            descricao = ""
            categoria = null
            impacto = null
            prioridade = IdeaPriority.MEDIUM
            viewModel.resetCreateState()
        }
    }

    fun validate(): Boolean {
        tituloError = if (titulo.isBlank()) "Informe o título da ideia" else null
        descricaoError = if (descricao.isBlank()) "Descreva sua ideia" else null
        return tituloError == null && descricaoError == null && categoria != null && impacto != null
    }

    AnimatedContent(
        targetState = submitted,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "form_transition",
    ) { isSubmitted ->
        if (isSubmitted) {
            SuccessContent(onNewIdeia = { submitted = false })
        } else {
            FormContent(
                titulo = titulo,
                onTituloChange = { titulo = it; tituloError = null },
                tituloError = tituloError,
                descricao = descricao,
                onDescricaoChange = { descricao = it; descricaoError = null },
                descricaoError = descricaoError,
                categoria = categoria,
                onCategoriaChange = { categoria = it },
                impacto = impacto,
                onImpactoChange = { impacto = it },
                prioridade = prioridade,
                onPrioridadeChange = { prioridade = it },
                isLoading = createState is IdeaCreateUiState.Loading,
                apiError = (createState as? IdeaCreateUiState.Error)?.message,
                onSubmit = {
                    focusManager.clearFocus()
                    if (validate()) {
                        viewModel.createIdea(titulo, descricao, categoria!!, impacto!!, prioridade)
                    }
                },
            )
        }
    }
}

@Composable
private fun SuccessContent(onNewIdeia: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppPrimaryCard {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Ideia enviada!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Sua ideia está em análise. Você pode acompanhar o status em \"Minhas Ideias\".",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(Spacing.md))
            Button(
                onClick = onNewIdeia,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(Radius.md),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Enviar outra ideia",
                    color = Primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FormContent(
    titulo: String,
    onTituloChange: (String) -> Unit,
    tituloError: String?,
    descricao: String,
    onDescricaoChange: (String) -> Unit,
    descricaoError: String?,
    categoria: String?,
    onCategoriaChange: (String) -> Unit,
    impacto: String?,
    onImpactoChange: (String) -> Unit,
    prioridade: IdeaPriority,
    onPrioridadeChange: (IdeaPriority) -> Unit,
    isLoading: Boolean,
    apiError: String?,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = "  Compartilhe sua Ideia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = "Suas ideias ajudam a empresa a inovar. Preencha os campos e envie.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                ModernTextField(
                    value = titulo,
                    onValueChange = onTituloChange,
                    label = "Título da ideia *",
                    isError = tituloError != null,
                    supportingText = tituloError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                )
                ModernTextField(
                    value = descricao,
                    onValueChange = onDescricaoChange,
                    label = "Descrição detalhada *",
                    isError = descricaoError != null,
                    supportingText = descricaoError ?: "Como funciona? Qual problema resolve?",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 8,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                    ),
                )
            }
        }

        item {
            AppCard {
                SectionLabel("Categoria *", categoria == null)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.padding(top = Spacing.xs),
                ) {
                    ideaCategories.forEach { cat ->
                        FilterChip(
                            selected = categoria == cat,
                            onClick = { onCategoriaChange(cat) },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            }
        }

        item {
            AppCard {
                SectionLabel("Impacto estimado *", impacto == null)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.padding(top = Spacing.xs),
                ) {
                    ideaImpacts.forEach { imp ->
                        val color = when (imp) {
                            "Alto" -> MaterialTheme.colorScheme.error
                            "Médio" -> Color(0xFFDD6B20)
                            else -> Color(0xFF38A169)
                        }
                        FilterChip(
                            selected = impacto == imp,
                            onClick = { onImpactoChange(imp) },
                            label = { Text(imp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.15f),
                                selectedLabelColor = color,
                            ),
                        )
                    }
                }
            }
        }

        item {
            AppCard {
                SectionLabel("Prioridade", false)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.padding(top = Spacing.xs),
                ) {
                    IdeaPriority.entries.forEach { p ->
                        FilterChip(
                            selected = prioridade == p,
                            onClick = { onPrioridadeChange(p) },
                            label = { Text(p.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            }
        }

        if (apiError != null) {
            item {
                AppCard {
                    Text(
                        text = apiError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        item {
            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                shape = RoundedCornerShape(Radius.md),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text("Enviar Ideia", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun SectionLabel(label: String, showRequired: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        if (showRequired) {
            Text(
                text = " · obrigatório",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
