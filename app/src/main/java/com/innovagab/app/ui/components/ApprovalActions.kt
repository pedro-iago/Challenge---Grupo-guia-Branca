package com.innovagab.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.innovagab.app.ui.theme.Error
import com.innovagab.app.ui.theme.Radius
import com.innovagab.app.ui.theme.Spacing
import com.innovagab.app.ui.theme.Success

@Composable
fun ApprovalActions(
    onApprove: () -> Unit,
    onReject: () -> Unit,
    isSubmitting: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        OutlinedButton(
            onClick = onReject,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(Radius.md),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
        ) {
            Text("Rejeitar", style = MaterialTheme.typography.labelLarge)
        }

        Button(
            onClick = onApprove,
            enabled = !isSubmitting,
            shape = RoundedCornerShape(Radius.md),
            colors = ButtonDefaults.buttonColors(containerColor = Success),
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )
            } else {
                Text("Aprovar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
