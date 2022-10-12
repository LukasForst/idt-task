package dev.forst.idt.dto

import java.util.UUID

// side note - it's a good practice to have data classes for data
// however, data classes are final and thus it's not really possible (in an elegant way)
// to share their properties with inheritance

/**
 * User entity.
 */
data class UserDto(
    val id: UUID,
    val name: String
)

/**
 * DTO used for updating user.
 */
data class UserUpdateDto(
    val name: String?,
)

/**
 * DTO used for creation of the user.
 */
data class UserCreationDto(
    val name: String
)
