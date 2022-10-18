package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.feature_editor.presentation.util.MeasureView
import com.phoenix.powerplayscorer.feature_editor.presentation.util.SegmentedButton

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TextButton (
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp),
    label: String,
    buttons: List<String>,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    activeIndex: Boolean?,
    specialColor: Boolean,
    visible: Boolean,
    enabled: Boolean,
    onClick: (Boolean?) -> Unit
) {
    val color = if (specialColor) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    val backgroundColor = if (specialColor)
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
    ) { targetState ->
        if (targetState) {
            Measure(
                modifier = Modifier.padding(paddingValues),
                label = label,
                buttons = buttons,
                textStyle = textStyle,
                enabled = enabled
            ) { modifier1, modifier2, height ->
                Text(modifier = modifier1, text = label, style = textStyle)
                AnimatedContent(
                    modifier = modifier2,
                    targetState = enabled,
                    transitionSpec = {
                        (fadeIn() with
                                fadeOut()).using(SizeTransform(clip = false))
                    }
                ) { targetState ->
                    if (targetState) {
                        SegmentedButton(
                            items = buttons,
                            selectedIndex = activeIndex.convert(),
                            color = color,
                            onItemClick = {
                                onClick(it.convert())
                            }
                        )
                    } else {
                        if (activeIndex == null) {
                            Surface(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .size(52.dp, 32.dp),
                                shape = CircleShape,
                                color = backgroundColor
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Not checked")
                            }
                        } else {
                            Surface(
                                color = backgroundColor,
                                modifier = Modifier.height(height),
                                shape = CircleShape,
                            ) {
                                Text(
                                    text = buttons[activeIndex.convert()],
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .widthIn(min = 94.dp)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .wrapContentHeight(),
                                )
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
    modifier: Modifier,
    label: String,
    buttons: List<String>,
    textStyle: TextStyle,
    enabled: Boolean,
    content: @Composable (modifier1: Modifier, modifier2: Modifier, height: Dp) -> Unit
) {
    MeasureView(
        modifier = modifier,
        viewToMeasure = listOf({
            Text(modifier = Modifier.width(IntrinsicSize.Min), text = label, style = textStyle)
        }, {
            SegmentedButton(items = buttons)
        })
    ) { maxWidth, measuredWidth, measuredHeight ->
        val vertical = maxWidth < measuredWidth[0] + if (enabled) measuredWidth[1] else 94.dp

        if (vertical) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content(Modifier, Modifier.align(Alignment.End), measuredHeight[1])
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content(Modifier.weight(1f), Modifier, measuredHeight[1])
            }
        }
    }
}

private fun Boolean?.convert(): Int? {
    return when (this) {
        true -> 1
        false -> 0
        else -> null
    }
}

private fun Boolean.convert(): Int {
    return when (this) {
        true -> 1
        false -> 0
    }
}

private fun Int?.convert(): Boolean? {
    return when (this) {
        1 -> true
        0 -> false
        else -> null
    }
}