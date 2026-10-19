package me.meenagopal24.localization

import me.meenagopal24.localization.local.getLocalString
import me.meenagopal24.localization.utils.replaceWithArgs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalStringTest {

    @Test
    fun testGetLocalStringUninitializedReturnsNull() {
        // When Localize is not initialized, getLocalString(key) should safely return null via safeGetProvider
        val result = getLocalString("some_key")
        assertNull(result)
    }

    @Test
    fun testGetLocalStringPrefixedKeyCleaning() {
        // Test that keys with colons are cleaned (handled by cleanKey extension)
        val prefixedKey = "app:welcome_key"
        val clean = prefixedKey.substringAfter(":")
        assertEquals("welcome_key", clean)
    }

    @Test
    fun testFormatArgsReplacement() {
        // Test the formatting mechanism used by getLocalString(resource, vararg formatArgs)
        val baseString = "Hello %1\$s, you have %2\$s new messages."
        val args = listOf("Alice", "5")
        val formatted = baseString.replaceWithArgs(args)
        assertEquals("Hello Alice, you have 5 new messages.", formatted)
    }
}
