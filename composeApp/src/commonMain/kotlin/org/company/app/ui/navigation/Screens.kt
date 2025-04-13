package org.company.app.ui.navigation

/**
 * Sealed class representing the different screens in the application.
 * Each screen is defined as a data object inheriting from this class.
 * This class provides a type-safe way to navigate between screens
 * by using the screen's path as a unique identifier.
 *
 * @property path The unique string identifier (path) for the screen.
 */
sealed class Screens(val path: String) {
    data object Login : Screens(path = "login")
    data object Home : Screens(path = "home")
    data object Details : Screens(path = "details")
}