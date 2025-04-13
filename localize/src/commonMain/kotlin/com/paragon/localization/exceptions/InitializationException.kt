package com.paragon.localization.exceptions

/**
 * This exception is thrown when the SDK is not properly initialized.
 */
class InitializationException(message: String = "please call `Localize.init()` before using Localize.") :
    Exception(message)