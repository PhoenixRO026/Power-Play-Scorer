package com.phoenix.powerplayscorer.feature_editor.presentation.login

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phoenix.powerplayscorer.feature_editor.presentation.login.components.TextField
import kotlinx.coroutines.flow.update
import com.phoenix.powerplayscorer.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToEditor: () -> Unit,
) {
    val state = viewModel.state.collectAsState()
    val mutableState = viewModel.state
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    var snackBarJob: Job? = null

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(all = 16.dp)
                    .animateContentSize(),
            ) {
                TextField(
                    label = "Email",
                    text = state.value.email,
                    keyboardOptions = remember {
                        KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(32.dp),
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email icon"
                        )
                    },
                    onValueChange = { newString ->
                        mutableState.update {
                            it.copy(
                                email = newString
                            )
                        }
                    }
                )
                TextField(
                    modifier = Modifier.padding(top = 8.dp),
                    label = "Password",
                    text = state.value.password,
                    keyboardOptions = remember {
                        KeyboardOptions(
                            autoCorrect = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = {
                                mutableState.update {
                                    it.copy(
                                        passVisible = !it.passVisible
                                    )
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp),
                                painter = if (state.value.passVisible)
                                    painterResource(id = R.drawable.visibility)
                                else painterResource(id = R.drawable.visibility_off),
                                contentDescription = "Password icon"
                            )
                        }
                    },
                    visualTransformation = if (state.value.passVisible)
                        VisualTransformation.None
                    else remember {
                        PasswordVisualTransformation()
                    },
                    onValueChange = { newString ->
                        mutableState.update {
                            it.copy(
                                password = newString
                            )
                        }
                    }
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    ),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        viewModel.login(
                            onSuccess = {
                                navigateToEditor()
                            },
                            onFailure = { message ->
                                snackBarJob?.cancel()
                                snackBarJob = scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message ?: "Login failed."
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                if (state.value.isLoginLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
                Box(
                    modifier = Modifier.padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 2.dp
                    )
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface
                            )
                            .padding(horizontal = 8.dp),
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    ),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        viewModel.register(
                            onSuccess = {
                                snackBarJob?.cancel()
                                snackBarJob = scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Register successful."
                                    )
                                }
                            },
                            onFailure = { message ->
                                snackBarJob?.cancel()
                                snackBarJob = scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message ?: "Register failed."
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                if (state.value.isRegisterLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

}