package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.feature_editor.presentation.util.MeasureView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TextSwitch (
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp),
    text: String,
    checked: Boolean,
    specialColor: Boolean,
    visible: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    val view = LocalView.current
    val colors = if (specialColor)
        SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onTertiary,
            checkedTrackColor = MaterialTheme.colorScheme.tertiary,
            checkedBorderColor = MaterialTheme.colorScheme.tertiary
        )
    else
        SwitchDefaults.colors()

    val colorChecked = if (specialColor)
        MaterialTheme.colorScheme.tertiary
    else MaterialTheme.colorScheme.primary

    val colorUnChecked = if (specialColor)
        MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.primaryContainer

    AnimatedContent(
        modifier = modifier,
        targetState = visible,
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentScope.SlideDirection.Down
            ) + fadeIn() with
                    slideOutOfContainer(
                        towards = AnimatedContentScope.SlideDirection.Up
                    ) + fadeOut()
        }
    ) { targetState1 ->
        if (targetState1) {
            Measure(
                modifier = Modifier.padding(paddingValues),
                text = text,
                textStyle = textStyle
            ) { modifier1, modifier2 ->
                Text(
                    modifier = modifier1,
                    text = text,
                    style = textStyle,
                    textAlign = TextAlign.Start
                )

                AnimatedContent(
                    targetState = enabled,
                    transitionSpec = {
                        if (targetState) {
                            slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with
                                    slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                        } else {
                            slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() with
                                    slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
                        }.using(
                            // Disable clipping since the faded slide-in/out should
                            // be displayed out of bounds.
                            SizeTransform(clip = false)
                        )
                    }
                ) { targetState2 ->
                    if (targetState2) {
                        Switch(
                            modifier = modifier2,
                            checked = checked,
                            colors = colors,
                            onCheckedChange = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                onChange(it)
                            }
                        )
                    } else {
                        Surface(
                            modifier = modifier2
                                .padding(vertical = 8.dp)
                                .size(52.dp, 32.dp),
                            color = if (checked)
                                colorChecked
                            else colorUnChecked,
                            shape = CircleShape
                        ) {
                            if (checked) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Checked")
                            } else {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Not checked")
                            }
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.fillMaxWidth())
        }
    }

}

@Composable
private fun Measure (
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    content: @Composable (modifier: Modifier, modifier2: Modifier) -> Unit
) {
    MeasureView(
        modifier = modifier,
        viewToMeasure = {
            Row {
                Text(text = text, style = textStyle, modifier = Modifier.width(IntrinsicSize.Min))
                Switch(checked = false, onCheckedChange = {})
            }
        }
    ) { maxWidth, measuredWidth, _ ->
        val vertical = maxWidth < measuredWidth

        if (vertical) {
            Column {
                content(Modifier, Modifier.align(Alignment.End))
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content(Modifier.weight(1f), Modifier)
            }
        }
    }
}