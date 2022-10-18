package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.presentation.editor.components.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.min
import com.phoenix.powerplayscorer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val editEnabled = viewModel.isEditEnabled.collectAsState()
    val isNewMatch by viewModel.isNewMatch.collectAsState()
    val screenList = remember {
        screenList(
            state = state,
            mutableState = viewModel.state,
            editEnabled = editEnabled
        )
    }
    val twoTeams by remember { derivedStateOf { state.value.twoTeams } }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (editEnabled.value) {
                        viewModel.save()
                    } else {
                        viewModel.edit()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (editEnabled.value)
                    Icon(painterResource(id = R.drawable.done), "Done")
                else Icon(Icons.Default.Edit, "Edit")
            }
        },
        topBar = {
            TopAppBar(
                checked = twoTeams,
                editEnabled = editEnabled.value,
                isNewMatch = isNewMatch,
                onCheckedChange = { newChecked ->
                    viewModel.state.update { match ->
                        match.copy(
                            twoTeams = newChecked
                        )
                    }
                },
                onReset = {
                    viewModel.reset()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(
                items = screenList
            ) { item ->
                item(Modifier)
            }
        }
    }
}

fun screenList(
    state: State<Match>,
    mutableState: MutableStateFlow<Match>,
    editEnabled: State<Boolean>
): List<@Composable (modifier: Modifier) -> Unit> {
    val autoUpperLimit by derivedStateOf {
        12 - state.value.let {
            it.autoTerminal + it.autoGroundJunction + it.autoLowJunction +
                    it.autoMediumJunction + it.autoHighJunction
        }
    }
    val driverUpperLimit by derivedStateOf {
        30 - state.value.let {
            it.driverTerminal + it.driverGroundJunction + it.driverLowJunction +
                    it.driverMediumJunction + it.driverHighJunction
        }
    }
    val endgameUpperLimit by derivedStateOf {
        25 - state.value.let {
            it.junctionsOwnedByCone + it.junctionsOwnedByBeacons
        }
    }
    val twoTeams by derivedStateOf { state.value.twoTeams }

    val autoPoints by derivedStateOf {
        state.value.let {
            it.autoTerminal +
                    it.autoGroundJunction * 2 +
                    it.autoLowJunction * 3 +
                    it.autoMediumJunction * 4 +
                    it.autoHighJunction * 5 +
                    when (it.autoParked1) {
                        false -> 2
                        true -> 10 * (it.customSignalSleeve1 + 1)
                        else -> 0
                    } +
                    when (it.autoParked2) {
                        false -> 2
                        true -> 10 * (it.customSignalSleeve2 + 1)
                        else -> 0
                    } * it.twoTeams
        }
    }
    val driverPoints by derivedStateOf {
        state.value.let {
            it.driverTerminal +
                    it.driverGroundJunction * 2 +
                    it.driverLowJunction * 3 +
                    it.driverMediumJunction * 4 +
                    it.driverHighJunction * 5
        }
    }
    val endgamePoints by derivedStateOf {
        state.value.let {
            it.junctionsOwnedByCone * 3 +
                    it.junctionsOwnedByBeacons * 10 +
                    it.circuitCompleted * 20 +
                    it.endParked1 * 2 +
                    it.endParked2 * 2 * it.twoTeams
        }
    }
    val totalPoints by derivedStateOf {
        autoPoints + driverPoints + endgamePoints
    }

    return listOf(
        { modifier ->
            val text by remember { derivedStateOf { state.value.title } }
            TextField(
                modifier = modifier,
                label = "Title",
                text = text,
                onValueChange = { newString ->
                    mutableState.update {
                        it.copy(
                            title = newString
                        )
                    }
                },
                enabled = editEnabled.value
            )
        },
        { modifier ->
            val activeIndex by remember { derivedStateOf { state.value.alliance } }
            AllianceButtons(
                modifier = modifier,
                firstText = "Red Alliance",
                secondText = "Blue Alliance",
                activeIndex = activeIndex,
                onButtonClicked = { index ->
                    mutableState.update { match ->
                        match.copy(
                            alliance = if (match.alliance == index) null else index
                        )
                    }
                },
                enabled = editEnabled.value
            )
        },
        { modifier ->
            Title(
                modifier = modifier,
                title = "Autonomous points: ",
                counter = autoPoints
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoTerminal } }
            TextCounter(
                modifier = modifier,
                text = "Cones in Terminal: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoTerminal = match.autoTerminal + add,
                            driverTerminal =
                            if (match.driverTerminal + add >= 0 && driverUpperLimit - add >= 0)
                                match.driverTerminal + add
                            else match.driverTerminal
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoGroundJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on Ground: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoGroundJunction = match.autoGroundJunction + add,
                            driverGroundJunction =
                            if (match.driverGroundJunction + add >= 0 && driverUpperLimit - add >= 0)
                                match.driverGroundJunction + add
                            else match.driverGroundJunction
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoLowJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on Low: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoLowJunction = match.autoLowJunction + add,
                            driverLowJunction =
                            if (match.driverLowJunction + add >= 0 && driverUpperLimit - add >= 0)
                                match.driverLowJunction + add
                            else match.driverLowJunction
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoMediumJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on Medium: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoMediumJunction = match.autoMediumJunction + add,
                            driverMediumJunction =
                            if (match.driverMediumJunction + add >= 0 && driverUpperLimit - add >= 0)
                                match.driverMediumJunction + add
                            else match.driverMediumJunction
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoHighJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on High: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoHighJunction = match.autoHighJunction + add,
                            driverHighJunction =
                            if (match.driverHighJunction + add >= 0 && driverUpperLimit - add >= 0)
                                match.driverHighJunction + add
                            else match.driverHighJunction
                        )
                    }
                }
            )
        },
        { modifier ->
            val activeIndex by remember { derivedStateOf { state.value.autoParked1 } }
            TextButton(
                modifier = modifier,
                label = if (twoTeams) "Parking 1: " else "Parking: ",
                buttons = listOf("Terminal or\nSubstation", "Signal Zone"),
                activeIndex = activeIndex,
                specialColor = false,
                visible = true,
                enabled = editEnabled.value,
                onClick = { newIndex ->
                    mutableState.update { match ->
                        match.copy(
                            autoParked1 = newIndex
                        )
                    }
                }
            )
        },
        { modifier ->
            val checked by remember { derivedStateOf { state.value.customSignalSleeve1 } }
            val visible by remember { derivedStateOf { state.value.autoParked1 == true } }
            TextSwitch(
                modifier = modifier,
                text = if (twoTeams) "Custom Sleeve 1: " else "Custom Sleeve: ",
                checked = checked,
                specialColor = false,
                visible = visible,
                enabled = editEnabled.value,
                onChange = {
                    mutableState.update { match ->
                        match.copy(
                            customSignalSleeve1 = it
                        )
                    }
                }
            )
        },
        { modifier ->
            val activeIndex by remember { derivedStateOf { state.value.autoParked2 } }
            TextButton(
                modifier = modifier,
                label = "Parking 2: ",
                buttons = listOf("Terminal or\nSubstation", "Signal Zone"),
                activeIndex = activeIndex,
                specialColor = true,
                visible = twoTeams,
                enabled = editEnabled.value,
                onClick = { newIndex ->
                    mutableState.update { match ->
                        match.copy(
                            autoParked2 = newIndex
                        )
                    }
                }
            )
        },
        { modifier ->
            val checked by remember { derivedStateOf { state.value.customSignalSleeve2 } }
            val visible by remember { derivedStateOf { state.value.autoParked2 == true && twoTeams } }
            TextSwitch(
                modifier = modifier,
                text = "Custom Sleeve 2: ",
                checked = checked,
                specialColor = true,
                visible = visible,
                enabled = editEnabled.value,
                onChange = {
                    mutableState.update { match ->
                        match.copy(
                            customSignalSleeve2 = it
                        )
                    }
                }
            )
        },
        { modifier ->
            Title(
                modifier = modifier,
                title = "Driver points: ",
                counter = driverPoints
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.driverTerminal } }
            TextCounter(
                modifier = modifier,
                text = "Cones in Terminal: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = driverUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            driverTerminal = match.driverTerminal + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.driverGroundJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on Ground: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = driverUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            driverGroundJunction = match.driverGroundJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.driverLowJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on Low: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = driverUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            driverLowJunction = match.driverLowJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.driverMediumJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on Medium: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = driverUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            driverMediumJunction = match.driverMediumJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.driverHighJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones on High: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = driverUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            driverHighJunction = match.driverHighJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            Title(
                modifier = modifier,
                title = "Endgame points: ",
                counter = endgamePoints
            )
        },
        { modifier ->
            val checked by remember { derivedStateOf { state.value.circuitCompleted } }
            TextSwitch(
                modifier = modifier,
                text = "Circuit completed: ",
                checked = checked,
                specialColor = false,
                visible = true,
                enabled = editEnabled.value,
                onChange = {
                    mutableState.update { match ->
                        match.copy(
                            circuitCompleted = it
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.junctionsOwnedByCone } }
            TextCounter(
                modifier = modifier,
                text = "Owned by Cone: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = endgameUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            junctionsOwnedByCone = match.junctionsOwnedByCone + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.junctionsOwnedByBeacons.toInt() } }
            TextCounter(
                modifier = modifier,
                text = "Owned by Beacon: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = min(endgameUpperLimit + counter, 2),
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            junctionsOwnedByBeacons = (match.junctionsOwnedByBeacons.toInt() + add).toBoolean()
                        )
                    }
                }
            )
        },
        { modifier ->
            val checked by remember { derivedStateOf { state.value.endParked1 } }
            TextSwitch(
                modifier = modifier,
                text = if (twoTeams) "Parking 1: " else "Parking: ",
                checked = checked,
                specialColor = false,
                visible = true,
                enabled = editEnabled.value,
                onChange = {
                    mutableState.update { match ->
                        match.copy(
                            endParked1 = it
                        )
                    }
                }
            )
        },
        { modifier ->
            val checked by remember { derivedStateOf { state.value.endParked2 } }
            TextSwitch(
                modifier = modifier,
                text = "Parking 2: ",
                checked = checked,
                specialColor = true,
                visible = twoTeams,
                enabled = editEnabled.value,
                onChange = {
                    mutableState.update { match ->
                        match.copy(
                            endParked2 = it
                        )
                    }
                }
            )
        },
        { modifier ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = modifier
                    .padding(top = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Title(
                        modifier = Modifier.weight(1f),
                        title = "Total points: ",
                        counter = totalPoints,
                        paddingValues = PaddingValues()
                    )
                    Spacer(modifier = Modifier.size(88.dp))
                }
            }
        }
    )
}

operator fun Boolean.plus(other: Int) = if (this) other + 1 else other
operator fun Boolean.times(other: Int) = if (this) other else 0
operator fun Int.times(other: Boolean) = if (other) this else 0

private operator fun Boolean?.plus(other: Int) = if (this == null) 0 else this + 1 + other
operator fun Boolean?.times(other: Int) = if (this == null) 0 else (this + 1) * other
private operator fun Int.times(other: Boolean?) = if (other == null) 0 else (other + 1) * this

private fun Boolean?.toInt(): Int = when (this) {
    true -> 2
    false -> 1
    else -> 0
}

private fun Int.toBoolean(): Boolean? = when (this) {
    2 -> true
    1 -> false
    else -> null
}

private operator fun Int.plus(other: Boolean?): Int {
    return this + when (other) {
        true -> 2
        false -> 1
        else -> 0
    }
}