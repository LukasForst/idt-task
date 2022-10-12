package dev.forst.idt.service

import dev.forst.idt.dto.UserCreationDto
import dev.forst.idt.dto.UserDto
import dev.forst.idt.dto.UserUpdateDto
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class UsersServiceTest {
    @Test
    fun `test getUser returns null when there is no user`() {
        assertNull(UsersService().getUser(UUID.randomUUID()))
    }

    @Test
    fun `test getUser returns correct user filled from first map`() {
        val user = UserDto(UUID.randomUUID(), "John")
        val service = UsersService(mutableMapOf(user.id to user))
        assertEquals(user, service.getUser(user.id))
    }

    @Test
    fun `test createUser creates a user with correct id and name`() {
        val expectedId = UUID.randomUUID()
        val userCreation = UserCreationDto("John")
        val service = UsersService(idAssignment = { expectedId })
        val createdUser = service.createUser(userCreation)
        assertEquals(expectedId, createdUser.id)
        assertEquals(userCreation.name, createdUser.name)
    }

    @Test
    fun `test getUser returns user after createUser was used`() {
        val userCreation = UserCreationDto("John")
        val service = UsersService()
        val createdUser = service.createUser(userCreation)
        assertEquals(createdUser, service.getUser(createdUser.id))
    }

    @Test
    fun `test updateUser updates user properly`() {
        val previousUser = UserDto(UUID.randomUUID(), "John")
        val service = UsersService(mutableMapOf(previousUser.id to previousUser))
        assertEquals(previousUser, service.getUser(previousUser.id))
        val newName = "Peter"
        val newUser = service.updateUser(previousUser.id, UserUpdateDto(newName))
        assertNotNull(newUser)
        assertEquals(newName, newUser.name)
        // assert rest
        val newExpectedUser = previousUser.copy(name = newName)
        assertEquals(newExpectedUser, newUser)
        assertEquals(newExpectedUser, service.getUser(newUser.id))
    }
}
