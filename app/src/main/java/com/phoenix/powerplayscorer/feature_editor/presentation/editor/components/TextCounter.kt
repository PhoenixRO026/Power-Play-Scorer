package com.phoenix.powerplayscorer.feature_editor.presentation.editor.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.R
import com.phoenix.powerplayscorer.feature_editor.presentation.util.MeasureView
import kotlinx.coroutines.delay


@Composable
fun TextCounter (
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp),
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    counter: Int,
    enabled: Boolean,
    lowerLimit: Int,
    upperLimit: Int,
    onClick: (add: Int) -> Unit
) {
    Measure(
        modifier = modifier.padding(paddingValues),
        counter = counter,
        textStyle = textStyle,
        text = text
    ) { modifier1, modifier2 ->
        Text(modifier = modifier1, text = text, style = textStyle)
        Counter(
            modifier = modifier2,
            counter = counter,
            textStyle = textStyle,
            onClick = onClick,
            enabled = enabled,
            lowerLimit = lowerLimit,
            upperLimit = upperLimit
        )
    }
}

@Composable
private fun Measure (
    modifier: Modifier,
    counter: Int,
    textStyle: TextStyle,
    text: String,
    content: @Composable (modifier1: Modifier, modifier2: Modifier) -> Unit,
) {
    MeasureView(
        modifier = modifier,
        viewToMeasure = {
            Row {
                Text(modifier = Modifier.width(IntrinsicSize.Min), text = text, style = textStyle)
                Counter(
                    counter = counter,
                    textStyle = textStyle,
                    onClick = {},
                    enabled = true
                )
            }
        },
    ) { maxWidth, measuredWidth, _ ->
        val vertical = maxWidth < measuredWidth

        if (vertical) {
            Column(Modifier.fillMaxWidth()) {
                content(Modifier, Modifier.align(Alignment.End))
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content(Modifier.weight(1f), Modifier)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Counter (
    modifier: Modifier = Modifier,
    counter: Int,
    textStyle: TextStyle,
    onClick: (add: Int) -> Unit,
    enabled: Boolean,
    lowerLimit: Int = 0,
    upperLimit: Int = 30,
) {
    val minus = painterResource(id = R.drawable.ic_baseline_minus_24)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text (
                modifier = Modifier
                    .widthIn(min = 52.dp)
                    .height(32.dp)
                    .wrapContentHeight(),
                text = "$counter",
                style = textStyle.copy(fontFeatureSettings = "tnum"),
                textAlign = TextAlign.Center
            )
        }

        AnimatedContent(
            targetState = enabled,
            transitionSpec = {
                (slideInHorizontally(initialOffsetX = { it }) with
                        slideOutHorizontally(targetOffsetX = { it }))
                    .using(
                        SizeTransform(
                            clip = false,
                            sizeAnimationSpec = { _, _ ->
                                spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    visibilityThreshold = IntSize.VisibilityThreshold
                                )
                            }
                        )
                    )
            }
        ) { targetState ->
            if (targetState) {
                Row {
                    CounterButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onClick(-1) },
                        enabled = counter > lowerLimit
                    ) {
                        Icon(minus , contentDescription = "Subtract")
                    }
                    CounterButton(
                        onClick = { onClick(1) },
                        enabled = counter < upperLimit
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun CounterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    maxDelayMillis: Long = 500,
    minDelayMillis: Long = 100,
    delayDecayFactor: Float = .2f,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val isPressed by interactionSource.collectIsPressedAsState()

    FilledIconButton(
        modifier = modifier
            .size(52.dp, 48.dp),
        onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onClick()
        },
        content = content,
        interactionSource = interactionSource,
        enabled = enabled
    )

    LaunchedEffect(key1 = isPressed, key2 = enabled) {
        var currentDelayMillis = maxDelayMillis

        while (enabled && isPressed) {
            onClick()
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            delay(currentDelayMillis)
            currentDelayMillis =
                (currentDelayMillis - (currentDelayMillis * delayDecayFactor))
                    .toLong().coerceAtLeast(minDelayMillis)
        }
    }
}