package com.paragon.localization.exceptions

/**
 * This exception is thrown when the SDK is not properly configured.
 *
 * In applications that rely on DVLocalize, certain configurations or initializations
 * are required before. If sdk is initialized without the necessary setup, this
 * exception is used to signal that the configuration is missing or incomplete.
 *
 * @param message A detailed message describing the cause of the exception. This can
 *                provide additional context about the missing configuration or
 *                steps that need to be taken to resolve the issue.
 */
class InvalidConfigurationException(message: String) : Exception(message)