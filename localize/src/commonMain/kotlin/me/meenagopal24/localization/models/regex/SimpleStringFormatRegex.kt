package me.meenagopal24.localization.models.regex

/**
 * Robust regex matching positional formatting tokens (e.g. `%1$s`, `%2$d`, `%1$@`).
 */
internal val PositionalFormatRegex = Regex("""%(\d+)\$[@sd]""")

/**
 * Regex matching sequential formatting tokens without positional indices (e.g. `%s`, `%d`, `%@`).
 */
internal val SequentialFormatRegex = Regex("""%[@sd]""")

/**
 * Backwards compatibility alias.
 */
internal val SimpleStringFormatRegex = PositionalFormatRegex

