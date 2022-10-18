package com.phoenix.powerplayscorer.feature_editor.presentation.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.phoenix.powerplayscorer.feature_editor.presentation.util.OutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField (
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        keyboardOptions = keyboardOptions,
        textStyle = MaterialTheme.typography.titleLarge,
        smallTextStyle = MaterialTheme.typography.titleMedium,
        enabled = enabled,
        visualTransformation = visualTransformation,
        singleLine = true,
        trailingIcon = trailingIcon,
        keyboardActions = keyboardActions
    )
}