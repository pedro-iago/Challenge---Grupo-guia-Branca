package com.innovagab.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innovagab.app.data.ideas.IdeaStatus
import com.innovagab.app.ui.theme.Success

val IdeaStatus.color: Color
    get() = when (this) {
        IdeaStatus.PENDING -> Color(0xFF3182CE)
        IdeaStatus.APPROVED -> Success
        IdeaStatus.IN_PROGRESS -> Color(0xFFDD6B20)
        IdeaStatus.REJECTED -> Color(0xFFE53E3E)
        IdeaStatus.COMPLETED -> Color(0xFF718096)
    }

@Composable
fun StatusChip(
    status: IdeaStatus,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = status.color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier,
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = status.color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
