package com.phoenix.powerplayscorer.feature_editor.presentation.list.components

import android.text.format.DateFormat
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import java.util.*
import com.phoenix.powerplayscorer.R.drawable

private fun getDate(timestamp: Long): String {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.timeInMillis = timestamp
    return DateFormat.format("HH:mm d MMM y", calendar).toString()
}

@Preview
@Composable
private fun ItemPreview() {
    ItemCard(
        item = Match(
            createStamp = 10000000,
            title = "Lorem ipsum\na",
            userId = "test",
            status = 1
        ),
        index = 1,
        onClick = {},
        onHold = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    item: Match,
    index: Int,
    onClick: () -> Unit,
    onHold: () -> Unit,
    failedStatusClick: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    infoStyle: TextStyle = MaterialTheme.typography.titleSmall
) {
    val view = LocalView.current

    val newIndex = index + 1
    val points = item.totalPoints

    
    Surface(
        modifier = modifier
            .fillMaxWidth(1f)
            .clip(shape = MaterialTheme.shapes.medium)
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
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(
                start = 12.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 4.dp
            ),
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
                DateCard(timestamp = item.createStamp, style = infoStyle)
                Text(
                    text = "$points points",
                    style = infoStyle
                )
            }
            if (item.userId != "offline") {
                Status(
                    uploaded = when (item.status) {
                        0 -> false
                        1 -> true
                        2 -> false
                        else -> null
                    },
                    failedStatusClick = failedStatusClick
                )
            }
        }
    }
}

@Composable
private fun Status(
    modifier: Modifier = Modifier,
    uploaded: Boolean?,
    failedStatusClick: () -> Unit = {},
) {
    if (uploaded == null) {
        FilledIconButton(
            modifier = modifier,
            onClick = failedStatusClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = drawable.sync_problem),
                contentDescription = "upload status"
            )
        }
    } else {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = when (uploaded) {
                    false -> painterResource(id = drawable.cloud_sync)
                    true -> painterResource(id = drawable.cloud_done)
                },
                contentDescription = "upload status"
            )
        }
    }
}

@Composable
private fun DateCard(
    timestamp: Long,
    style: TextStyle
) {
    val date = getDate(timestamp)
    Text(
        text = date,
        style = style
    )
}