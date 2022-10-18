package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    editEnabled: Boolean,
    isNewMatch: Boolean
) {
    val view = LocalView.current
    val iconColors = IconButtonDefaults.iconButtonColors(
        contentColor = contentColorFor(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = if (editEnabled) {
                    if (isNewMatch) "New Match" else "Edit Match"
                } else "View Match",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            AnimatedVisibility(
                visible = editEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onReset()
                    },
                    colors = iconColors
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Reset", modifier = Modifier.size(32.dp))
                }
            }
        },
        actions = {
            TeamsNrIcon(
                checked = checked,
                onCheckedChange = onCheckedChange,
                editEnabled = editEnabled
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}