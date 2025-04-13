package me.meenagopal24.localization.utils

import me.meenagopal24.localization.models.regex.SimpleStringFormatRegex

/**
 * Replaces placeholders in the string with corresponding values from the provided list of arguments.
 */
internal fun String.replaceWithArgs(args: List<String>) = SimpleStringFormatRegex.replace(this) { matchResult ->
    args[matchResult.groupValues[1].toInt() - 1]
}
