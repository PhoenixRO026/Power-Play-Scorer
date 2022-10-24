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
import com.phoenix.powerplayscorer.R
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.presentation.editor.components.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState(MappedEditorState())
    val screenList = remember {
        screenList(
            state = state,
            mutableState = viewModel.savableState
        )
    }
    val twoTeams by remember { derivedStateOf { state.value.twoTeams } }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (state.value.isEditEnabled) {
                        viewModel.save()
                    } else {
                        viewModel.edit()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (state.value.isEditEnabled)
                    Icon(painterResource(id = R.drawable.done), "Done")
                else Icon(Icons.Default.Edit, "Edit")
            }
        },
        topBar = {
            TopAppBar(
                checked = twoTeams,
                editEnabled = state.value.isEditEnabled,
                isNewMatch = state.value.isNewMatch,
                onCheckedChange = { newChecked ->
                    viewModel.savableState.update { editorState ->
                        val match = editorState.match
                        editorState.copy(
                            match = match.copy(
                                twoTeams = newChecked
                            )
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
    state: State<MappedEditorState>,
    mutableState: MutableStateFlow<EditorState>,
): List<@Composable (modifier: Modifier) -> Unit> {
    return listOf(
        { modifier ->
            TextField(
                modifier = modifier,
                label = "Title",
                text = state.value.match.title,
                onValueChange = { newString ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            title = newString
                        )
                    }
                },
                enabled = state.value.isEditEnabled
            )
        },
        { modifier ->
            AllianceButtons(
                modifier = modifier,
                firstText = "Red Alliance",
                secondText = "Blue Alliance",
                activeIndex = state.value.match.alliance,
                onButtonClicked = { index ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            alliance = if (match.alliance == index) null else index
                        )
                    }
                },
                enabled = state.value.isEditEnabled
            )
        },
        { modifier ->
            Title(
                modifier = modifier,
                title = "Autonomous points: ",
                counter = state.value.matchTotals.autoTotal
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Cones in Terminal: ",
                counter = state.value.match.autoTerminal,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.autoPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        val driverUpperLimit = driverUpperLimits(match)
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
            TextCounter(
                modifier = modifier,
                text = "Cones on Ground: ",
                counter = state.value.match.autoGroundJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.autoPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        val driverUpperLimit = driverUpperLimits(match)
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
            TextCounter(
                modifier = modifier,
                text = "Cones on Low: ",
                counter = state.value.match.autoLowJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.autoPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        val driverUpperLimit = driverUpperLimits(match)
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
            TextCounter(
                modifier = modifier,
                text = "Cones on Medium: ",
                counter = state.value.match.autoMediumJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.autoPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        val driverUpperLimit = driverUpperLimits(match)
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
            TextCounter(
                modifier = modifier,
                text = "Cones on High: ",
                counter = state.value.match.autoHighJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.autoPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        val driverUpperLimit = driverUpperLimits(match)
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
            TextButton(
                modifier = modifier,
                label = if (state.value.twoTeams) "Parking 1: " else "Parking: ",
                buttons = listOf("Terminal or\nSubstation", "Signal Zone"),
                activeIndex = state.value.match.autoParked1,
                specialColor = false,
                visible = true,
                enabled = state.value.isEditEnabled,
                onClick = { newIndex ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            autoParked1 = newIndex
                        )
                    }
                }
            )
        },
        { modifier ->
            val text by remember{
                derivedStateOf {
                    if (state.value.twoTeams) "Custom Sleeve 1: " else "Custom Sleeve: "
                }
            }
            TextSwitch(
                modifier = modifier,
                text = text,
                checked = state.value.match.customSignalSleeve1,
                specialColor = false,
                visible = state.value.matchVisibility.customSignalSleeve1,
                enabled = state.value.isEditEnabled,
                onChange = {
                    mutableState.updateMatch { match ->
                        match.copy(
                            customSignalSleeve1 = it
                        )
                    }
                }
            )
        },
        { modifier ->
            TextButton(
                modifier = modifier,
                label = "Parking 2: ",
                buttons = listOf("Terminal or\nSubstation", "Signal Zone"),
                activeIndex = state.value.match.autoParked2,
                specialColor = true,
                visible = state.value.twoTeams,
                enabled = state.value.isEditEnabled,
                onClick = { newIndex ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            autoParked2 = newIndex
                        )
                    }
                }
            )
        },
        { modifier ->
            TextSwitch(
                modifier = modifier,
                text = "Custom Sleeve 2: ",
                checked = state.value.match.customSignalSleeve2,
                specialColor = true,
                visible = state.value.matchVisibility.customSignalSleeve2,
                enabled = state.value.isEditEnabled,
                onChange = {
                    mutableState.updateMatch { match ->
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
                counter = state.value.matchTotals.driverTotal
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Cones in Terminal: ",
                counter = state.value.match.driverTerminal,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.driverPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            driverTerminal = match.driverTerminal + add
                        )
                    }
                }
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Cones on Ground: ",
                counter = state.value.match.driverGroundJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.driverPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            driverGroundJunction = match.driverGroundJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Cones on Low: ",
                counter = state.value.match.driverLowJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.driverPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            driverLowJunction = match.driverLowJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Cones on Medium: ",
                counter = state.value.match.driverMediumJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.driverPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            driverMediumJunction = match.driverMediumJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Cones on High: ",
                counter = state.value.match.driverHighJunction,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.driverPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
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
                counter = state.value.matchTotals.endgameTotal
            )
        },
        { modifier ->
            TextSwitch(
                modifier = modifier,
                text = "Circuit completed: ",
                checked = state.value.match.circuitCompleted,
                specialColor = false,
                visible = true,
                enabled = state.value.isEditEnabled,
                onChange = {
                    mutableState.updateMatch { match ->
                        match.copy(
                            circuitCompleted = it
                        )
                    }
                }
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Owned by Cone: ",
                counter = state.value.match.junctionsOwnedByCone,
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.endgameConePlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            junctionsOwnedByCone = match.junctionsOwnedByCone + add
                        )
                    }
                }
            )
        },
        { modifier ->
            TextCounter(
                modifier = modifier,
                text = "Owned by Beacon: ",
                counter = state.value.match.junctionsOwnedByBeacons.toInt(),
                enabled = state.value.isEditEnabled,
                lowerLimit = 0,
                plusEnabled = state.value.matchLimits.endgameBeaconPlusEnabled,
                onClick = { add ->
                    mutableState.updateMatch { match ->
                        match.copy(
                            junctionsOwnedByBeacons = (match.junctionsOwnedByBeacons.toInt() + add).toBoolean()
                        )
                    }
                }
            )
        },
        { modifier ->
            val text by remember {
                derivedStateOf {
                    if (state.value.twoTeams) "Parking 1: " else "Parking: "
                }
            }
            TextSwitch(
                modifier = modifier,
                text = text,
                checked = state.value.match.endParked1,
                specialColor = false,
                visible = true,
                enabled = state.value.isEditEnabled,
                onChange = {
                    mutableState.updateMatch { match ->
                        match.copy(
                            endParked1 = it
                        )
                    }
                }
            )
        },
        { modifier ->
            TextSwitch(
                modifier = modifier,
                text = "Parking 2: ",
                checked = state.value.match.endParked2,
                specialColor = true,
                visible = state.value.twoTeams,
                enabled = state.value.isEditEnabled,
                onChange = {
                    mutableState.updateMatch { match ->
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
                        counter = state.value.matchTotals.total,
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

private fun MutableStateFlow<EditorState>.updateMatch(
    body: (match: Match) -> Match
) {
    this.update { editorState ->
        val match = editorState.match
        editorState.copy(
            match = body(match)
        )
    }
}