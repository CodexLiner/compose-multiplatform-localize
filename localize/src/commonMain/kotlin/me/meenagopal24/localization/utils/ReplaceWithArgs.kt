package me.meenagopal24.localization.utils

import me.meenagopal24.localization.models.regex.PositionalFormatRegex
import me.meenagopal24.localization.models.regex.SequentialFormatRegex

/**
 * Replaces placeholders in the string with corresponding values from the provided list of arguments.
 * Supports both positional (%1$s, %2$d) and sequential (%s, %d) tokens with safe out-of-bounds fallback.
 */
internal fun String.replaceWithArgs(args: List<String>): String {
    if (args.isEmpty()) return this

    var hasPositional = false
    val positionalReplaced = PositionalFormatRegex.replace(this) { matchResult ->
        hasPositional = true
        val index = (matchResult.groupValues[1].toIntOrNull() ?: 1) - 1
        args.getOrNull(index) ?: matchResult.value
    }

    if (hasPositional) return positionalReplaced

    var seqIndex = 0
    return SequentialFormatRegex.replace(this) { matchResult ->
        val replacement = args.getOrNull(seqIndex) ?: matchResult.value
        seqIndex++
        replacement
    }
}
