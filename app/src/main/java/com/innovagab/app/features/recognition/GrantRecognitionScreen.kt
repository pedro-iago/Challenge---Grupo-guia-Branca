package com.innovagab.app.features.recognition

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.innovagab.app.data.gamification.BadgeType
import com.innovagab.app.ui.components.AppCard
import com.innovagab.app.ui.components.AppPrimaryButton
import com.innovagab.app.ui.components.ModernTextField
import com.innovagab.app.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrantRecognitionScreen(
    viewModel: RecognitionViewModel,
    onSuccess: () -> Unit,
) {
    val grantState by viewModel.grantState.collectAsStateWithLifecycle()

    var selectedUser by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedBadge by remember { mutableStateOf<BadgeType?>(null) }
    var note by remember { mutableStateOf("") }
    var userDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(grantState.grantSuccess) {
        if (grantState.grantSuccess) {
            viewModel.resetGrantState()
            onSuccess()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            AppCard {
                FormSectionLabel("Colaborador")
                Spacer(Modifier.height(Spacing.sm))
                ExposedDropdownMenuBox(
                    expanded = userDropdownExpanded,
                    onExpandedChange = { userDropdownExpanded = it },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    ) {
                        AppCard(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = selectedUser?.second ?: "Selecione um colaborador",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedUser != null) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = userDropdownExpanded,
                        onDismissRequest = { userDropdownExpanded = false },
                    ) {
                        if (grantState.users.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Nenhum usuário disponível",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                                onClick = {},
                            )
                        }
                        grantState.users.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AvatarCircle(
                                            initials = user.second.split(" ")
                                                .filter { it.isNotBlank() }
                                                .take(2)
                                                .joinToString("") { it.first().uppercase() },
                                            color = MaterialTheme.colorScheme.primary,
                                            size = 28,
                                        )
                                        Spacer(Modifier.width(Spacing.sm))
                                        Text(
                                            text = user.second,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                },
                                onClick = {
                                    selectedUser = user
                                    userDropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
        }

        item {
            AppCard {
                FormSectionLabel("Tipo de reconhecimento")
                Spacer(Modifier.height(Spacing.sm))
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    BadgeType.manualBadges.forEach { badge ->
                        val isSelected = selectedBadge == badge
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBadge = if (isSelected) null else badge },
                            label = {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = badge.icon(),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(Modifier.width(Spacing.xs))
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        )
                                    }
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        item {
            AppCard {
                FormSectionLabel("Mensagem de reconhecimento")
                Spacer(Modifier.height(Spacing.sm))
                ModernTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Mensagem (opcional)",
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    supportingText = "Uma mensagem pessoal para tornar o reconhecimento ainda mais especial",
                )
            }
        }

        if (grantState.error != null) {
            item {
                AppCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
                    Text(
                        text = grantState.error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        item {
            val isValid = selectedUser != null && selectedBadge != null
            AppPrimaryButton(
                text = "Conceder Reconhecimento",
                onClick = {
                    val user = selectedUser ?: return@AppPrimaryButton
                    val badge = selectedBadge ?: return@AppPrimaryButton
                    viewModel.grantRecognition(user.first, user.second, badge, note.trim())
                },
                enabled = isValid,
                isLoading = grantState.isGranting,
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
