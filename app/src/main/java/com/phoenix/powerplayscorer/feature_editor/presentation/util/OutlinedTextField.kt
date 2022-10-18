package com.phoenix.powerplayscorer.feature_editor.presentation.util

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.lerp

@ExperimentalMaterial3Api
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    smallTextStyle: TextStyle,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.outlinedShape,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = {
            if (label != null) {
                val isEmpty = remember(value, visualTransformation) {
                    visualTransformation.filter(AnnotatedString(value)).text.text.isEmpty()
                }

                MyLabel(
                    isEmpty = isEmpty,
                    interactionSource = interactionSource,
                    label = label,
                    bodyLarge = textStyle,
                    bodySmall = smallTextStyle
                )
            }
        },
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyLabel (
    isEmpty: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    label: @Composable (() -> Unit),
    bodySmall: TextStyle = MaterialTheme.typography.bodySmall,
    bodyLarge: TextStyle = MaterialTheme.typography.bodyLarge,
) {

    val isFocused = interactionSource.collectIsFocusedAsState().value

    val inputState = when {
        isFocused -> InputPhase.Focused
        isEmpty -> InputPhase.UnfocusedEmpty
        else -> InputPhase.UnfocusedNotEmpty
    }

    val transition = updateTransition(inputState, label = "TextFieldInputState")

    val labelProgress by transition.animateFloat(
        label = "LabelProgress",
        transitionSpec = { tween(durationMillis = AnimationDuration) }
    ) {
        when (it) {
            InputPhase.Focused -> 1f
            InputPhase.UnfocusedEmpty -> 0f
            InputPhase.UnfocusedNotEmpty -> 1f
        }
    }

    val labelTextStyle = lerp(
        bodyLarge,
        bodySmall,
        labelProgress
    )

    ProvideTextStyle(
        value = labelTextStyle,
        content = label
    )
}

private enum class InputPhase {
    // Text field is focused
    Focused,

    // Text field is not focused and input text is empty
    UnfocusedEmpty,

    // Text field is not focused but input text is not empty
    UnfocusedNotEmpty
}

internal const val AnimationDuration = 150 //150