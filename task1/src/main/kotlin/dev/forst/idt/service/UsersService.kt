package dev.forst.idt.service

import dev.forst.idt.dto.UserCreationDto
import dev.forst.idt.dto.UserDto
import dev.forst.idt.dto.UserUpdateDto
import java.util.Collections
import java.util.UUID

/**
 * Service taking care of user entity in the system.
 */
class UsersService(
    // this would be replaced by database & another user entity,
    // but assignments says it's ok to store in memory
    // we inject this via constructor so we can properly mock and test this in the unit tests
    private val users: MutableMap<UUID, UserDto> = Collections.synchronizedMap(mutableMapOf()),
    private val idAssignment: () -> UUID = { UUID.randomUUID() }
) {

    /**
     * Returns [UserDto] with given [userId] if found, otherwise returns null.
     */
    fun getUser(userId: UUID): UserDto? = users[userId]

    /**
     * Applies given change set [userUpdate] to user object with ID [userId].
     * Returns updated entity or null if the user was not found.
     */
    fun updateUser(userId: UUID, userUpdate: UserUpdateDto): UserDto? =
        users.computeIfPresent(userId) { _, user ->
            user.copy(
                name = userUpdate.name ?: user.name
            )
        }

    /**
     * Creates new user from [newUser] and returns [UserDto].
     */
    fun createUser(newUser: UserCreationDto): UserDto = UserDto(
        id = idAssignment(),
        name = newUser.name
    ).also { users[it.id] = it }
}
