package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.feature_editor.presentation.util.OutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField (
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp),
    label: String,
    text: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    val colors = TextFieldDefaults.outlinedTextFieldColors(
        //disabledBorderColor = MaterialTheme.colorScheme.outline,
        //disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTextColor = MaterialTheme.colorScheme.onSurface
    )
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues),
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(autoCorrect = false),
        textStyle = MaterialTheme.typography.titleLarge,
        smallTextStyle = MaterialTheme.typography.titleMedium,
        enabled = enabled,
        colors = colors
    )
}