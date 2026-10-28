package me.meenagopal24.localization

import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.models.enums.FileType
import me.meenagopal24.localization.providers.krivan.KrivanConfig
import me.meenagopal24.localization.providers.krivan.KrivanConstants
import me.meenagopal24.localization.providers.krivan.KrivanEnvironment
import me.meenagopal24.localization.providers.krivan.KrivanLocalizeLanguageProvider
import me.meenagopal24.localization.providers.krivan.KrivanPayloadParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KrivanProviderTest {

    @Test
    fun testKrivanConfigDefaults() {
        val config = KrivanConfig(
            projectId = "test-proj-uuid",
            apiKey = "loc_live_12345"
        )

        assertEquals("test-proj-uuid", config.projectId)
        assertEquals("loc_live_12345", config.apiKey)
        assertEquals(FileType.JSON, config.fileType)
        assertTrue(config.enableLiveOtaSync)
        assertTrue(config.enableEtagCaching)
        assertEquals(KrivanEnvironment.PRODUCTION, config.environment)
        assertEquals(KrivanConstants.PROD_BASE_URL, config.effectiveBaseUrl)
    }

    @Test
    fun testKrivanConfigValidation() {
        assertFailsWith<IllegalArgumentException> {
            KrivanConfig(projectId = "", apiKey = "key")
        }
        assertFailsWith<IllegalArgumentException> {
            KrivanConfig(projectId = "id", apiKey = "")
        }
    }

    @Test
    fun testKrivanPayloadParserWithEnvelopeJson() {
        val parser = KrivanPayloadParser()
        val envelopeJson = """
            {
                "projectId": "test-project-123",
                "locale": "en-US",
                "versionId": "ver-abc",
                "sequence": 1,
                "translations": {
                    "login.title": "Welcome Back",
                    "login.subtitle": "Sign in to continue"
                }
            }
        """.trimIndent()

        val parsed = parser.parse(envelopeJson, FileType.JSON)
        assertEquals("Welcome Back", parsed.languages["login.title"])
        assertEquals("Sign in to continue", parsed.languages["login.subtitle"])
    }

    @Test
    fun testKrivanPayloadParserWithFlatJson() {
        val parser = KrivanPayloadParser()
        val flatJson = """
            {
                "app.name": "Krivan App",
                "app.tagline": "Realtime Localization"
            }
        """.trimIndent()

        val parsed = parser.parse(flatJson, FileType.JSON)
        assertEquals("Krivan App", parsed.languages["app.name"])
        assertEquals("Realtime Localization", parsed.languages["app.tagline"])
    }

    @Test
    fun testKrivanPayloadParserWithAndroidXml() {
        val parser = KrivanPayloadParser()
        val xml = """
            <resources>
                <string name="cta_submit">Submit Application</string>
            </resources>
        """.trimIndent()

        val parsed = parser.parse(xml, FileType.XML)
        assertEquals("Submit Application", parsed.languages["cta_submit"])
    }

    @Test
    fun testKrivanLocalizeLanguageProviderMinimalConstruction() {
        val providerConfig = ProviderConfig(
            defaultLanguage = "en",
            supportedLanguages = listOf("en", "hi")
        )

        // Minimal caller setup requiring ONLY required information
        val provider = KrivanLocalizeLanguageProvider(
            projectId = "test-uuid",
            apiKey = "loc_live_abc",
            providerConfig = providerConfig
        )

        assertEquals("test-uuid", provider.krivanConfig.projectId)
        assertEquals("loc_live_abc", provider.krivanConfig.apiKey)
        assertEquals("en", provider.config.defaultLanguage)
        assertEquals(2, provider.config.supportedLanguages.size)
        provider.stopLiveOtaSync()
    }
}

