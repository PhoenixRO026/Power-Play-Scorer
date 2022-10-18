package com.phoenix.powerplayscorer.feature_editor.presentation.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@Composable
fun MeasureView (
    modifier: Modifier = Modifier,
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (maxWidth: Dp, measuredWidth: Dp, measuredHeight: Dp) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        MeasureViewInternal(viewToMeasure = listOf(viewToMeasure)) { measuredWidth, measuredHeight ->
            content(
                measuredWidth = measuredWidth[0],
                measuredHeight = measuredHeight[0],
                maxWidth = maxWidth
            )
        }
    }
}

@Composable
fun MeasureView (
    modifier: Modifier = Modifier,
    viewToMeasure: List<@Composable () -> Unit>,
    content: @Composable (maxWidth: Dp, measuredWidths: List<Dp>, measuredHeights: List<Dp>) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        MeasureViewInternal(
            viewToMeasure = viewToMeasure
        ) { newMeasuredWidths, newMeasuredHeights ->
            content(
                maxWidth = maxWidth,
                measuredWidths = newMeasuredWidths,
                measuredHeights = newMeasuredHeights,
            )
        }
    }
}

@Composable
fun MeasureViewInternal (
    viewToMeasure: List<@Composable () -> Unit>,
    content: @Composable (measuredWidths: List<Dp>, measuredHeights: List<Dp>) -> Unit
) {
    val measuredWidths = mutableListOf<Dp>()
    val measuredHeights = mutableListOf<Dp>()
    SubcomposeLayout { constraints ->
        viewToMeasure.forEachIndexed { index, viewToMeasure ->
            val measurement = subcompose("viewToMeasure$index", viewToMeasure)[0]
                .measure(Constraints())
            measuredWidths.add(measurement.width.toDp())
            measuredHeights.add(measurement.height.toDp())
        }
        val contentPlaceable = subcompose("content") {
            content(measuredWidths, measuredHeights)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}