package dev.forst.idt.error

import java.util.UUID

/**
 * Exception indicating that the user was not found in the application.
 */
data class UserNotFoundException(
    /**
     * User ID that was queried.
     */
    val userId: UUID,
    override val message: String = "User with $userId was not found!"
) : Exception(message)
