package com.phoenix.powerplayscorer.feature_editor.presentation.list.components

import android.text.format.DateFormat
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import java.util.*

private fun getDate(timestamp: Long): String {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.timeInMillis = timestamp
    return DateFormat.format("HH:mm d MMM y", calendar).toString()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    item: Match,
    index: Int,
    onClick: () -> Unit,
    onHold: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    infoStyle: TextStyle = MaterialTheme.typography.titleSmall
) {
    val view = LocalView.current

    val newIndex = index + 1
    val points = item.totalPoints
    val date = getDate(item.createStamp)
    
    Surface(
        modifier = modifier
            .fillMaxWidth(1f)
            .clip(shape = MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onClick()
                },
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onHold()
                },
            ),
        color = containerColor,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$newIndex. ",
                style = titleStyle
            )
            Text(
                text = item.title,
                modifier = Modifier
                    .weight(2f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = titleStyle
            )

            Column(
                horizontalAlignment = Alignment.End
            ){
                Text(
                    text = date,
                    style = infoStyle
                )
                Text(
                    text = "$points points",
                    style = infoStyle
                )
            }
        }
    }
}