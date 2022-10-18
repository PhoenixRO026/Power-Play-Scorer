package com.phoenix.powerplayscorer.feature_editor.presentation.auth

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phoenix.powerplayscorer.feature_editor.presentation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navigate: (path: String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToEditor: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (viewModel.isUserLoggedIn())
            navigateToEditor()
    }

    val view = LocalView.current

    Scaffold { paddingValues ->
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
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    ),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        navigate(Screen.LoginScreen.route)
                    }
                ) {
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    ),
                    onClick = {
                        /*viewModel.singInOffline(
                            onSuccess = {
                                navigateToEditor()
                            },
                            onFailure = {

                            }
                        )*/
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        navigateToEditor()
                    }
                ) {
                    Text(
                        text = "Continue offline",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}