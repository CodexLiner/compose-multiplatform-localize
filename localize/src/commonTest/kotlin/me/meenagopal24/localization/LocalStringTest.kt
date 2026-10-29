package me.meenagopal24.localization

import me.meenagopal24.localization.local.getLocalString
import me.meenagopal24.localization.utils.replaceWithArgs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalStringTest {

    @Test
    fun testGetLocalStringUninitializedReturnsNull() {
        val result = getLocalString("some_key")
        assertNull(result)
    }

    @Test
    fun testGetLocalStringPrefixedKeyCleaning() {
        val prefixedKey = "app:welcome_key"
        val clean = prefixedKey.substringAfter(":")
        assertEquals("welcome_key", clean)
    }

    @Test
    fun testFormatArgsReplacement() {
        val baseString = "Hello %1\$s, you have %2\$s new messages."
        val args = listOf("Alice", "5")
        val formatted = baseString.replaceWithArgs(args)
        assertEquals("Hello Alice, you have 5 new messages.", formatted)
    }

    @Test
    fun testSequentialFormatArgsReplacement() {
        val baseString = "Welcome %s, total items: %d"
        val args = listOf("Gopal", "42")
        val formatted = baseString.replaceWithArgs(args)
        assertEquals("Welcome Gopal, total items: 42", formatted)
    }

    @Test
    fun testSafeFormatArgsOutOfBoundsFallback() {
        val baseString = "Hello %1\$s, role: %2\$s, score: %3\$s"
        val args = listOf("Alice")
        val formatted = baseString.replaceWithArgs(args)
        assertEquals("Hello Alice, role: %2\$s, score: %3\$s", formatted)
    }
}
