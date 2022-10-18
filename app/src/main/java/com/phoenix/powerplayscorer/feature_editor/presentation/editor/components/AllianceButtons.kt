package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.feature_editor.presentation.util.MeasureView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllianceButtons (
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(
        top = 16.dp,
        start = 16.dp,
        end = 16.dp,
        bottom = 8.dp
    ),
    firstText: String,
    secondText: String,
    activeIndex: Boolean?,
    onButtonClicked: (index: Boolean) -> Unit,
    enabled: Boolean,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {

    val redActive = activeIndex?.not() ?: false
    val blueActive = activeIndex ?: false
    val redColor by animateColorAsState(
        targetValue = if (redActive)
            Color.Red
        else if (enabled)
            MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )
    val blueColor by animateColorAsState(
        targetValue = if (blueActive)
            Color.Blue
        else if (enabled)
            MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    )
    val redTextColor by animateColorAsState(
        targetValue = if (redActive)
            Color.White
        else if (enabled)
            contentColorFor(backgroundColor = MaterialTheme.colorScheme.secondaryContainer)
        else contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant)
    )
    val blueTextColor by animateColorAsState(
        targetValue = if (blueActive)
            Color.White
        else if (enabled)
            contentColorFor(backgroundColor = MaterialTheme.colorScheme.secondaryContainer)
        else contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant)
    )

    val view = LocalView.current

    Measure(
        modifier = modifier
            .padding(paddingValues)
            .fillMaxWidth(),
        firstText = firstText,
        secondText = secondText,
        textStyle = textStyle,
    ) { newModifier, vertical ->
        Surface(
            modifier = newModifier,
            color = redColor,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onButtonClicked(false)
            },
            shape = MaterialTheme.shapes.large,
            contentColor = redTextColor,
            enabled = enabled
        ) {
            Text(
                text = firstText,
                textAlign = TextAlign.Center,
                style = textStyle,
                modifier = Modifier
                    .heightIn(min = 60.dp)
                    .padding(all = 8.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(),
            )
        }

        if (vertical)
            Spacer(modifier = Modifier.height(8.dp))
        else
            Spacer(modifier = Modifier.width(16.dp))

        Surface(
            modifier = newModifier,
            color = blueColor,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onButtonClicked(true)
            },
            shape = MaterialTheme.shapes.large,
            contentColor = blueTextColor,
            enabled = enabled
        ) {
            Text(
                text = secondText,
                textAlign = TextAlign.Center,
                style = textStyle,
                modifier = Modifier
                    .heightIn(min = 60.dp)
                    .padding(all = 8.dp)
                    .fillMaxHeight()
                    .wrapContentHeight(),
            )
        }
    }
}

@Composable
private fun Measure (
    modifier: Modifier = Modifier,
    firstText: String,
    secondText: String,
    textStyle: TextStyle,
    content: @Composable (modifier: Modifier, vertical: Boolean) -> Unit
) {
    MeasureView(
        modifier = modifier,
        viewToMeasure = {
            Row(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Max)
            ) {
                Text(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .weight(1f),
                    text = firstText,
                    style = textStyle,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .weight(1f),
                    text = secondText,
                    style = textStyle,
                )
            }
        }
    ) { maxWidth, measuredWidth, _ ->
        val vertical = maxWidth < measuredWidth

        if (vertical) {
            Column {
                content(Modifier.fillMaxWidth(), true)
            }
        } else {
            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                content(Modifier.weight(1f), false)
            }
        }
    }
}