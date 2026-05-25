package com.innovagab.app.features.recognition

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.innovagab.app.data.gamification.BadgeType

internal fun BadgeType.icon(): ImageVector = when (this) {
    BadgeType.PIONEIRO -> Icons.Default.Stars
    BadgeType.INOVADOR -> Icons.Default.Lightbulb
    BadgeType.SOLUCIONADOR -> Icons.Default.CheckCircle
    BadgeType.TRANSFORMADOR -> Icons.Default.TrendingUp
    BadgeType.COLABORADOR -> Icons.Default.Groups
    BadgeType.DESTAQUE_MES -> Icons.Default.MilitaryTech
    BadgeType.AGENTE_MUDANCA -> Icons.Default.AutoAwesome
    BadgeType.LIDER_INOVACAO -> Icons.Default.VerifiedUser
}
