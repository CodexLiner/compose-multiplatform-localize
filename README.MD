---

# Localization Setup Guide

This document provides a complete guide to setting up dynamic localization using the `meenagopal24.localization` library for both Jetpack Compose and View-based Android applications.

---

## 📦 Installation

Add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("me.meenagopal24:localize:1.0.0")
}
```

---

## 🔧 Initialization

### ✅ Common Initialization (Compose & View-based)

The initialization logic is identical for both Jetpack Compose and traditional View-based projects. You must call:

```kotlin
Localize.init(MyOwnLanguageProvider(providerConfig))
```

| UI Type             | Where to Call                                  |
|---------------------|-------------------------------------------------|
| Jetpack Compose     | Inside a top-level `@Composable`, e.g., `App()` |
| View-based (XML)    | Inside `onCreate()` of `Application` class      |

---

## 🔌 Custom Language Provider

Create a custom provider by extending `LocalizeLanguageProvider`:

```kotlin
class MyOwnLanguageProvider(providerConfig: ProviderConfig) : LocalizeLanguageProvider(providerConfig) {
    override suspend fun getLanguage(code: String): LocalLanguage? {
        // Implement logic to fetch a specific language file
    }

    override suspend fun getLanguages(): List<LocalLanguageList?> {
        // Implement logic to fetch the list of all supported languages
    }
}
```

---

## ⚙️ Configuration

Create a `ProviderConfig` instance with your app details:

```kotlin
val providerConfig = ProviderConfig(
    appName = "YourAppName",
    defaultLanguage = "en",
    supportedLanguages = listOf("en", "hi", "ar", "fr", "uk")
)
```

---

## 🧩 Compose Example

```kotlin
@Composable
fun App() = AppTheme {
    key(Unit) {
        val providerConfig = ProviderConfig(
            appName = "YourAppName",
            defaultLanguage = "en",
            supportedLanguages = listOf("en", "hi", "ar", "fr", "uk")
        )
        Localize.init(MyOwnLanguageProvider(providerConfig))
    }

    NavigationHost(
        navHostController = rememberNavController(),
        startDestination = Screens.Home.path
    )
}
```

---

## 🧱 View-based Example

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val providerConfig = ProviderConfig(
            appName = "YourAppName",
            defaultLanguage = "en",
            supportedLanguages = listOf("en", "hi", "ar", "fr", "uk")
        )
        Localize.init(MyOwnLanguageProvider(providerConfig))
    }
}
```

---

## 🌐 Accessing Localized Strings

### In Jetpack Compose:

To access localized strings in Jetpack Compose, use the `localStringResources` function and pass the string resource ID along with any dynamic values (if needed):

```kotlin
Text(localStringResources(R.string.enter_valid_name_email, "ndsdn"))
```

- **Explanation**: This example demonstrates how to access a string resource in Jetpack Compose using `localStringResources`. The first argument is the string resource ID (`R.string.enter_valid_name_email`), and the second argument is a dynamic value (`"ndsdn"`) to be injected into the localized string.

### In View-based UI:

```kotlin
textView.text = Localize.getString("home.title")
```

---

## 🔄 Language Management Functions

### 1. **Change Language**

To change the language at runtime, call the `setCurrentLanguage` function:

```kotlin
Localize.setCurrentLanguage("fr") // Change to French
```

- **Explanation**: This method sets the current language for your application by providing the language code (e.g., `"fr"` for French).

### 2. **Get Current Language Code**

To retrieve the code of the currently set language, use:

```kotlin
val currentLanguage = Localize.getCurrentLanguage()
```

- **Explanation**: This method returns the current language code (e.g., `"en"` for English, `"fr"` for French).

### 3. **Force Update Languages**

To forcefully redownload all the available languages, use:

```kotlin
Localize.forceUpdateLanguages()
```

- **Explanation**: This method forces the language provider to reload and refresh the list of available languages, ensuring that the latest set of languages is available in the app.

---

## 🧠 Other Localize Functions

The `Localize` object provides several utility functions for interacting with localization features:

### `Localize.setCurrentLanguage(code: String)`
Sets the currently active language.

### `Localize.getCurrentLanguage(): String?`
Returns the current active language code.

### `Localize.forceUpdateLanguages()`
Forces a refresh of the list of available languages.

---

## 📝 Notes
- Always initialize `Localize` before accessing any localized content.
- Language changes will recompose your UI automatically in Compose. For View-based projects, you may need to refresh views manually.

---

## 📚 License

This library is provided by [meenagopal24](https://github.com/meenagopal24/localization). See the repository for license and contributions.

---