package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsNrIcon(
    modifier: Modifier = Modifier,
    checked: Boolean,
    editEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (checked)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.primaryContainer
    )
    val strokeColor by animateColorAsState(
        targetValue = if (checked)
            MaterialTheme.colorScheme.tertiary
        else
            MaterialTheme.colorScheme.primary
    )
    val view = LocalView.current
    Surface(
        modifier = modifier,
        checked = checked,
        enabled = editEnabled,
        onCheckedChange = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onCheckedChange(it)
        },
        shape = CircleShape,
        color = color,
        border = BorderStroke(1.dp, strokeColor)
    ) {
        Text(
            modifier = Modifier
                .size(32.dp)
                .wrapContentHeight(),
            text = if (checked) "2" else "1",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )
    }
}