package com.phoenix.powerplayscorer.feature_editor.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.auth.AuthUseCases
import com.phoenix.powerplayscorer.ui.theme.PowerPlayScorerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authUseCases: AuthUseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerPlayScorerTheme(
                dynamicColor = true
            ) {
                val start = if (authUseCases.isUserSignedIn())
                    "editor"
                else "login"
                Navigation(start)
            }
        }
    }
}