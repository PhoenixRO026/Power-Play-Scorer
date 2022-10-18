package com.phoenix.powerplayscorer.feature_editor.presentation

sealed class Screen(val route: String) {
    object ListScreen: Screen("list_screen")
    object EditorScreen: Screen("editor_screen")
    object ChooseAccount: Screen("choose_account")
    object LoginScreen: Screen("login_screen")
    object SettingsScreen: Screen("settings_screen")

    fun withArgs(vararg args: String): String {
        return  buildString {
            append(route)
            args.forEach { arg ->
                append("?key=$arg")
            }
        }
    }
}
