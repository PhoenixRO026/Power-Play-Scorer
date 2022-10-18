package com.phoenix.powerplayscorer.feature_editor.presentation.util

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * @param items List of items to be rendered
 * @param selectedIndex The index of selected item, this is null for no item selected
 * @param onItemClick Called when an item is clicked, with that items index as the parameter
 * @param color Background color of the selected item
 * @param enabled Enabled state of this button
 * @param vertical Makes the buttons vertically stacked for a more compact size
 */
@Composable
fun SegmentedButton(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int? = null,
    onItemClick: (index: Int?) -> Unit = {},
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    vertical: Boolean = false
) {
    if (!vertical) {
        Row(
            modifier = modifier
                .widthIn(min = 188.dp)
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Max)
        ) {
            SegmentedButtonComponents(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                items = items,
                selectedIndex = selectedIndex,
                onItemClick = {
                    if (it == selectedIndex)
                        onItemClick(null)
                    else onItemClick(it)
                },
                color = color,
                enabled = enabled,
                vertical = false
            )
        }
    } else {
        Column(
            modifier = modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
        ) {
            SegmentedButtonComponents(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                items = items,
                selectedIndex = selectedIndex,
                onItemClick = {
                    if (it == selectedIndex)
                        onItemClick(null)
                    else onItemClick(it)
                },
                color = color,
                enabled = enabled,
                vertical = true
            )
        }
    }
}

@Composable
private fun SegmentedButtonComponents (
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int? = null,
    onItemClick: (index: Int) -> Unit = {},
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    vertical: Boolean = false
) {

    val startShape = if (vertical)
        RoundedCornerShape(
            topStartPercent = 50,
            topEndPercent = 50,
            bottomStartPercent = 0,
            bottomEndPercent = 0
        )
    else
        RoundedCornerShape(
            topStartPercent = 50,
            topEndPercent = 0,
            bottomStartPercent = 50,
            bottomEndPercent = 0
        )
    val squareShape = RoundedCornerShape(
        topStartPercent = 0,
        topEndPercent = 0,
        bottomStartPercent = 0,
        bottomEndPercent = 0
    )
    val endShape = if (vertical)
        RoundedCornerShape(
            topStartPercent = 0,
            topEndPercent = 0,
            bottomStartPercent = 50,
            bottomEndPercent = 50
        )
    else
        RoundedCornerShape(
            topStartPercent = 0,
            topEndPercent = 50,
            bottomStartPercent = 0,
            bottomEndPercent = 50
        )
    val textModifier = if (vertical)
        Modifier.width(IntrinsicSize.Min)
    else
        Modifier.wrapContentHeight()

    val view = LocalView.current

    items.forEachIndexed { index, item ->
        val selected = selectedIndex == index
        val surfaceColor by animateColorAsState(
            if (selected) color else Color.Transparent,
        )
        val contentColor by animateColorAsState(
            if (selected) contentColorFor(backgroundColor = color) else MaterialTheme.colorScheme.onSurface,
        )
        OutlinedButton(
            modifier = modifier,
            shape = when (index) {
                0 -> startShape
                items.size - 1 -> endShape
                else -> squareShape
            },
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = surfaceColor,
                contentColor = contentColor
            ),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onItemClick(index)
            },
            contentPadding = when (index) {
                0 -> PaddingValues(
                    top = 4.dp,
                    bottom = 4.dp,
                    start = 8.dp,
                    end = 4.dp
                )
                items.size - 1 -> PaddingValues(
                    top = 4.dp,
                    bottom = 4.dp,
                    start = 4.dp,
                    end = 8.dp
                )
                else -> PaddingValues(
                    all = 4.dp
                )
            },
            enabled = enabled,
        ) {
            Text(
                text = item,
                textAlign = TextAlign.Center,
                modifier = textModifier
            )
        }
    }
}