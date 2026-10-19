package me.meenagopal24.localization

import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.enums.FileType
import me.meenagopal24.localization.utils.parseJsonToLocalLanguage
import me.meenagopal24.localization.utils.parseStringsToLocalLanguage
import me.meenagopal24.localization.utils.parseXmlToLocalLanguage
import me.meenagopal24.localization.utils.replaceWithArgs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ParserTest {

    @Test
    fun testXmlParserWithStringAndStringArray() {
        val xml = """
            <resources>
                <string name="welcome">Welcome to <b>App</b></string>
                <string-array name="planets_array">
                    <item>Mercury <b>1st</b></item>
                    <item>Venus</item>
                    <item>Earth <i>home</i></item>
                    <item>Mars</item>
                </string-array>
            </resources>
        """.trimIndent()

        val localLanguage = parseXmlToLocalLanguage(xml)

        assertEquals("Welcome to <b>App</b>", localLanguage.languages["welcome"])

        val array = localLanguage.stringArrays["planets_array"]
        assertNotNull(array)
        assertEquals(4, array.size)
        assertEquals("Mercury <b>1st</b>", array[0])
        assertEquals("Venus", array[1])
        assertEquals("Earth <i>home</i>", array[2])
        assertEquals("Mars", array[3])
    }

    @Test
    fun testXmlParserWithCdataSection() {
        val xml = """
            <resources>
                <string name="html_text"><![CDATA[Hello <b>World</b>]]></string>
                <string-array name="formatted_array">
                    <item><![CDATA[Item 1 <b>Bold</b>]]></item>
                    <item><![CDATA[Item 2 <i>Italic</i>]]></item>
                </string-array>
            </resources>
        """.trimIndent()

        val localLanguage = parseXmlToLocalLanguage(xml)

        assertEquals("Hello <b>World</b>", localLanguage.languages["html_text"])

        val array = localLanguage.stringArrays["formatted_array"]
        assertNotNull(array)
        assertEquals(2, array.size)
        assertEquals("Item 1 <b>Bold</b>", array[0])
        assertEquals("Item 2 <i>Italic</i>", array[1])
    }

    @Test
    fun testFlatJsonParserWithStringArrayAndHtml() {
        val json = """
            {
                "greeting": "Hello <b>User</b>",
                "days_array": [
                    "Monday <b>1</b>",
                    "Tuesday <i>2</i>",
                    "Wednesday"
                ]
            }
        """.trimIndent()

        val localLanguage = parseJsonToLocalLanguage(json)

        assertEquals("Hello <b>User</b>", localLanguage.languages["greeting"])

        val array = localLanguage.stringArrays["days_array"]
        assertNotNull(array)
        assertEquals(3, array.size)
        assertEquals("Monday <b>1</b>", array[0])
        assertEquals("Tuesday <i>2</i>", array[1])
        assertEquals("Wednesday", array[2])
    }

    @Test
    fun testFlatTeluguJsonParsing() {
        val json = """
            {
              "generate": "రూపొందించండి",
              "label_onboarding_animation": "ఆన్‌బోర్డింగ్ యానిమేషన్",
              "share_pdf_screen_message": "గమనిక: ట్రాన్సాక్షన్స్ స్క్రీన్ నుండి మీ అన్ని ఇన్‌వాయిస్‌లు మరియు వోచర్లను నిర్వహించండి మరియు ట్రాక్ చేయండి",
              "hint_list": [
                "ఉదా: మార్క్ నుండి 5000 వచ్చాయి",
                "ఉదా: మార్క్‌కు 5000 చెల్లించాం",
                "ఉదా: ట్రేడింగ్ ఖాతా",
                "ఉదా: ఏప్రిల్ నెల ట్రేడింగ్ ఖాతా",
                "ఉదా: బ్యాలెన్స్ షీట్",
                "ఉదా: లాభ నష్టాల ఖాతా (Profit & Loss)",
                "ఉదా: మార్క్ ఖాతా వివరాలు",
                "ఉదా: మార్క్ ఖాతా సారాంశం",
                "ఉదా: మార్క్ కోసం జాన్ అని అలియాస్ సృష్టించండి",
                "ఉదా: మార్క్ ఖాతాను సృష్టించండి"
              ]
            }
        """.trimIndent()

        val localLanguage = parseJsonToLocalLanguage(json)

        assertEquals("రూపొందించండి", localLanguage.languages["generate"])
        assertEquals("ఆన్‌బోర్డింగ్ యానిమేషన్", localLanguage.languages["label_onboarding_animation"])

        val hintList = localLanguage.stringArrays["hint_list"]
        assertNotNull(hintList)
        assertEquals(10, hintList.size)
        assertEquals("ఉదా: మార్క్ నుండి 5000 వచ్చాయి", hintList[0])
        assertEquals("ఉదా: మార్క్ ఖాతాను సృష్టించండి", hintList[9])
    }

    @Test
    fun testStructuredJsonParserWithStringArray() {
        val json = """
            {
                "languages": {
                    "app_title": "My Application"
                },
                "stringArrays": {
                    "colors": ["Red", "Green", "Blue"]
                }
            }
        """.trimIndent()

        val localLanguage = parseJsonToLocalLanguage(json)

        assertEquals("My Application", localLanguage.languages["app_title"])

        val array = localLanguage.stringArrays["colors"]
        assertNotNull(array)
        assertEquals(3, array.size)
        assertEquals("Red", array[0])
        assertEquals("Green", array[1])
        assertEquals("Blue", array[2])
    }

    @Test
    fun testAppleStringsParser() {
        val content = """
            "welcome_key" = "Welcome <b>Home</b>";
            "cancel_button" = "Cancel";
        """.trimIndent()

        val localLanguage = parseStringsToLocalLanguage(content)

        assertEquals("Welcome <b>Home</b>", localLanguage.languages["welcome_key"])
        assertEquals("Cancel", localLanguage.languages["cancel_button"])
    }

    @Test
    fun testLocalLanguageFactoryMethodsAndMultiLanguageSupport() {
        // Test English JSON via LocalLanguage.fromJson / parse
        val enJson = """{ "title": "Welcome", "items": ["One", "Two"] }"""
        val enLang = LocalLanguage.parse(enJson, FileType.JSON)
        assertEquals("Welcome", enLang.languages["title"])
        assertEquals(2, enLang.stringArrays["items"]?.size)

        // Test Hindi XML via LocalLanguage.fromXml / parse
        val hiXml = """
            <resources>
                <string name="title">स्वागत है</string>
                <string-array name="items"><item>एक</item><item>दो</item></string-array>
            </resources>
        """.trimIndent()
        val hiLang = LocalLanguage.parse(hiXml, FileType.XML)
        assertEquals("स्वागत है", hiLang.languages["title"])
        assertEquals("एक", hiLang.stringArrays["items"]?.get(0))

        // Test Telugu strings via LocalLanguage.fromStrings / parse
        val teStrings = """"title" = "స్వాగతం";"""
        val teLang = LocalLanguage.parse(teStrings, FileType.STRING)
        assertEquals("స్వాగతం", teLang.languages["title"])
    }

    @Test
    fun testColonKeyPrefixStripping() {
        // Test XML with colon prefix in name (e.g. "app:welcome")
        val xml = """
            <resources>
                <string name="app:welcome">Welcome</string>
                <string-array name="module:items"><item>A</item><item>B</item></string-array>
            </resources>
        """.trimIndent()
        val xmlLang = parseXmlToLocalLanguage(xml)
        assertEquals("Welcome", xmlLang.languages["welcome"])
        assertEquals(listOf("A", "B"), xmlLang.stringArrays["items"])

        // Test Apple strings with colon prefix
        val stringsContent = """"app:cancel" = "Cancel";"""
        val stringsLang = parseStringsToLocalLanguage(stringsContent)
        assertEquals("Cancel", stringsLang.languages["cancel"])
    }

    @Test
    fun testStringReplaceWithArgs() {
        val template = "Hello %1\$s, welcome to %2\$s!"
        val formatted = template.replaceWithArgs(listOf("Alice", "KMPLocalize"))
        assertEquals("Hello Alice, welcome to KMPLocalize!", formatted)
    }

    @Test
    fun testMalformedJsonHandling() {
        val malformedJson = "{ invalid_json }"
        val lang = parseJsonToLocalLanguage(malformedJson)
        assertNotNull(lang)
        assertEquals(0, lang.languages.size)
        assertEquals(0, lang.stringArrays.size)
    }

    @Test
    fun testCleanKeyLogic() {
        val keyWithPrefix = "app:welcome_message"
        val cleanKey = keyWithPrefix.substringAfter(":")
        assertEquals("welcome_message", cleanKey)

        val keyWithoutPrefix = "welcome_message"
        val cleanKey2 = keyWithoutPrefix.substringAfter(":")
        assertEquals("welcome_message", cleanKey2)
    }
}
