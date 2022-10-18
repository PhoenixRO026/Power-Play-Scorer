package com.phoenix.powerplayscorer.feature_editor.presentation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.phoenix.powerplayscorer.feature_editor.presentation.auth.AuthScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.editor.EditorScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.list.ListScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.login.LoginScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.settings.SettingsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    startDestination: String
) {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        loginGraph(navController)
        editorGraph(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.editorGraph(navController: NavController) {
    navigation(startDestination = Screen.ListScreen.route, route = "editor") {
        composable(
            route = Screen.ListScreen.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.LoginScreen.route -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
                    Screen.ChooseAccount.route -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
                    else -> slideIntoContainer(AnimatedContentScope.SlideDirection.Right)
                }
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
            }
        ) {
            ListScreen(
                navigate = {
                    navController.navigate(it)
                }
            )
        }
        composable(
            route = Screen.EditorScreen.route + "?key={key}",
            arguments = listOf(
                navArgument(name = "key") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
            }
        ) {
            EditorScreen()
        }
        composable(
            route = Screen.SettingsScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
            }
        ) {
            SettingsScreen(
                navigateToLogIn = {
                    navController.navigate("login") {
                        popUpTo("editor") {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = Screen.ChooseAccount.route, route = "login") {
        composable(
            route = Screen.LoginScreen.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.ListScreen.route -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
                    else -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
                }
            }
        ) {
            LoginScreen(
                navigateToEditor = {
                    navController.navigate("editor") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.ChooseAccount.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Right)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
            }
        ) {
            AuthScreen(
                navigate = {
                    navController.navigate(it)
                },
                navigateToEditor = {
                    navController.navigate("editor") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}