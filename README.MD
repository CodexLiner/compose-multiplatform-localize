# Localization Setup Guide

This guide will help you set up dynamic localization in your Kotlin Multiplatform (KMP), Jetpack Compose, and View-based Android applications using the `meenagopal24.localization` library.

---

## 🌐 Accessing Localized Strings

### **In Jetpack Compose**

To display localized strings in Jetpack Compose, you can use the `localStringResources` function, which takes a string resource ID and automatically fetches the appropriate translation:

```kotlin
Text(localStringResources(R.string.enter_valid_name_email))
```

**Explanation:** This will display the localized version of the string defined in your `R.string.enter_valid_name_email` resource file. Jetpack Compose automatically handles language changes and recomposition of your UI when the language is updated.

---

### **In Kotlin Multiplatform (KMP)**

In Kotlin Multiplatform (KMP), use the same `localStringResources` function, but with a slight variation in the syntax for accessing resources. The KMP-specific implementation for accessing localized strings is:

```kotlin
Text(localStringResources(Res.string.enter_valid_name_email))
```

**Explanation:** Here, `Res.string.enter_valid_name_email` refers to the resource ID of the string in your shared `Res` object. KMP allows you to access resources across different platforms (e.g., Android, iOS, etc.).

---

### **In View-based Android (XML)**

For traditional View-based Android apps (XML), you can fetch and display string resources like this:

```kotlin
textView.text = getString(R.string.title)
```

**Explanation:** This fetches the string resource `R.string.title` and assigns it to the `TextView`. You can easily access and display any localized string resource.

---

## 📦 Installation

To add the `meenagopal24.localization` library to your project, add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("me.meenagopal24:localize:1.0.0")
}
```

---

## 🔧 Initialization

### Common Initialization

The process to initialize localization is the same across Jetpack Compose, View-based Android apps, and Kotlin Multiplatform (KMP). Call the following initialization code:

```kotlin
Localize.init(MyOwnLanguageProvider(providerConfig))
```

#### Where to Call

| UI Type             | Where to Call                                   |
|---------------------|-------------------------------------------------|
| **Jetpack Compose** | Inside a top-level `@Composable`, like `App()` |
| **View-based**      | Inside `onCreate()` of your `Application` class |
| **Kotlin Multiplatform** | Inside a shared module or platform-specific code |

---

## 🔌 Custom Language Provider

By default, **Tolgee** is used as the language provider. If you prefer to create a custom provider, you can extend `LocalizeLanguageProvider` to fetch language data in your own way.

### Using Tolgee (Default Provider)

If you're using **Tolgee**, you can initialize it like this:

```kotlin
val providerConfig = ProviderConfig(
    appName = "YourAppName",
    defaultLanguage = "en",
    supportedLanguages = listOf("en", "hi", "ar", "fr", "uk")
)

val tolgeeLanguageProvider = TolgeeLanguageProvider("your-api-key", "en", providerConfig)
Localize.init(tolgeeLanguageProvider)
```

### Creating Your Own Language Provider

You can also create your own language provider by extending `LocalizeLanguageProvider` and implementing the required methods to fetch languages:

```kotlin
class MyOwnLanguageProvider(providerConfig: ProviderConfig) : LocalizeLanguageProvider(providerConfig) {
    override suspend fun getLanguage(code: String): LocalLanguage? {
        // Implement the logic to fetch language based on the code (e.g., "en", "fr")
    }

    override suspend fun getLanguages(): List<LocalLanguageList?> {
        // Implement the logic to fetch the list of all supported languages
    }
}
```

---

## ⚙️ Configuration

Define the `ProviderConfig` with your app details, including the name, default language, and supported languages:

```kotlin
val providerConfig = ProviderConfig(
    appName = "YourAppName",
    defaultLanguage = "en",
    supportedLanguages = listOf("en", "hi", "ar", "fr", "uk")
)
```

---

## 🧩 Jetpack Compose Example

Here’s how to set up localization in a Jetpack Compose app:

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

For a traditional View-based Android app, initialize localization in the `onCreate()` method of your `Application` class:

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

## 🔄 Language Management

### 1. **Change Language**

To change the app's language at runtime, use:

```kotlin
Localize.setCurrentLanguage("fr") // Change language to French
```

**Explanation:** This will set the current language to French (or any other supported language).

### 2. **Get Current Language Code**

To get the current language code, use:

```kotlin
val currentLanguage = Localize.getCurrentLanguage()
```

**Explanation:** This returns the current language code, e.g., `"en"` for English or `"fr"` for French.

### 3. **Force Update Languages**

To force an update of all available languages, use:

```kotlin
Localize.forceUpdateLanguages()
```

**Explanation:** This will refresh the available languages and ensure that the latest languages are available in the app.

---

## 🧠 Other Useful Functions

- `Localize.setCurrentLanguage(code: String)` – Sets the currently active language.
- `Localize.getCurrentLanguage(): String?` – Gets the current active language code.
- `Localize.forceUpdateLanguages()` – Forces a refresh of available languages.

---

## 📝 Notes

- Always initialize `Localize` before accessing localized content.
- Language changes will automatically update the UI in Jetpack Compose. For View-based Android apps, you may need to refresh views manually.
- The library supports **Kotlin Multiplatform (KMP)**, allowing you to share localization logic across Android, iOS, and other platforms.

---

## 📚 License

This library is maintained by [meenagopal24](https://github.com/meenagopal24/localization). See the repository for the license and contribution guidelines.