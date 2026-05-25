package com.innovagab.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovagab.app.data.ideas.IdeaPriority
import com.innovagab.app.ui.theme.Error
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success
import com.innovagab.app.ui.theme.Warning

val IdeaPriority.selectorColor: Color
    get() = when (this) {
        IdeaPriority.HIGH -> Error
        IdeaPriority.MEDIUM -> Warning
        IdeaPriority.LOW -> Success
    }

@Composable
fun PrioritySelector(
    selected: IdeaPriority,
    onSelect: (IdeaPriority) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        IdeaPriority.entries.forEach { priority ->
            val color = priority.selectorColor
            FilterChip(
                selected = selected == priority,
                onClick = { if (enabled) onSelect(priority) },
                enabled = enabled,
                label = {
                    Text(
                        text = priority.label,
                        fontWeight = if (selected == priority) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.15f),
                    selectedLabelColor = color,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
