package com.phoenix.powerplayscorer.feature_editor.presentation.edit.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Title(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(top = 8.dp),
    title: String,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    counter: Int,
    surfaceColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Surface(
        modifier = modifier
            .padding(paddingValues),
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = textStyle,
                textAlign = TextAlign.Start
            )
            Text(
                text = "$counter",
                style = textStyle.copy(fontFeatureSettings = "tnum"),
                textAlign = TextAlign.End
            )
        }
    }
}