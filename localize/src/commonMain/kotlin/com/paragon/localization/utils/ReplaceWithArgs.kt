package com.paragon.localization.utils

import com.paragon.localization.models.regex.SimpleStringFormatRegex

/**
 * Replaces placeholders in the string with corresponding values from the provided list of arguments.
 */
internal fun String.replaceWithArgs(args: List<String>) = SimpleStringFormatRegex.replace(this) { matchResult ->
    args[matchResult.groupValues[1].toInt() - 1]
}
