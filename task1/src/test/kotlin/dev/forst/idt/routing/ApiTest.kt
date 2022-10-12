package dev.forst.idt.routing

import dev.forst.idt.dto.UserCreationDto
import dev.forst.idt.dto.UserDto
import dev.forst.idt.dto.UserUpdateDto
import dev.forst.idt.service.UsersService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.kodein.di.bindSingleton
import java.util.UUID
import kotlin.test.assertEquals

class ApiTest : TestBase() {

    @Test
    fun `test get existing user returns correct user`() = testApplication {
        val existingUser = UserDto(UUID.randomUUID(), "Freddy")
        // create mock of the user service
        val mockedService = mockk<UsersService>()
        every { mockedService.getUser(existingUser.id) } returns existingUser
        // now inject it into the application
        val client = prepareApplication { bindSingleton { mockedService } }
        // and test it
        val response = client.getUser(existingUser.id)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(existingUser, response.body())

        verify(exactly = 1) { mockedService.getUser(existingUser.id) }
    }

    @Test
    fun `test get non existent user returns 404`() = testApplication {
        val requestedUser = UUID.randomUUID()
        // create mock of the user service
        val mockedService = mockk<UsersService>()
        // no users available
        every { mockedService.getUser(requestedUser) } returns null

        val client = prepareApplication { bindSingleton { mockedService } }
        val response = client.getUser(requestedUser)
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(ErrorResponse("User with $requestedUser was not found!"), response.body())

        verify(exactly = 1) { mockedService.getUser(requestedUser) }
    }

    @Test
    fun `test updating users data updates data`() = testApplication {
        val existingUser = UserDto(UUID.randomUUID(), "Freddy")
        val expectedUpdate = UserUpdateDto(name = "Johny")
        val expectedUser = existingUser.copy(name = expectedUpdate.name!!)
        val mockedService = mockk<UsersService>()
        every { mockedService.updateUser(existingUser.id, expectedUpdate) } returns expectedUser

        val client = prepareApplication { bindSingleton { mockedService } }

        val response = client.updateUser(existingUser.id, expectedUpdate)
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedUser, response.body())

        verify(exactly = 1) { mockedService.updateUser(existingUser.id, expectedUpdate) }
    }

    @Test
    fun `test updating users data for non existent user returns 404`() = testApplication {
        val userId = UUID.randomUUID()
        val expectedUpdate = UserUpdateDto(name = "Johny")
        val mockedService = mockk<UsersService>()
        every { mockedService.updateUser(userId, expectedUpdate) } returns null

        val client = prepareApplication { bindSingleton { mockedService } }

        val response = client.updateUser(userId, expectedUpdate)
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(ErrorResponse("User with $userId was not found!"), response.body())

        verify(exactly = 1) { mockedService.updateUser(userId, expectedUpdate) }
    }


    @Test
    fun `test creating new user returns user data`() = testApplication {
        // first create data
        val userCreation = UserCreationDto(name = "John")
        val expectedDto = UserDto(id = UUID.randomUUID(), name = userCreation.name)
        // then mock service
        val mockedService = mockk<UsersService>()
        every { mockedService.createUser(userCreation) } returns expectedDto

        val client = prepareApplication { bindSingleton { mockedService } }

        val createdUser = client.createUser(userCreation)
        assertEquals(expectedDto, createdUser)

        verify(exactly = 1) { mockedService.createUser(userCreation) }
    }


    // executes get user, does not check response
    private suspend fun HttpClient.getUser(userId: UUID) =
        get("/user/${userId}")

    // creates user and checks if it was created
    private suspend fun HttpClient.updateUser(userId: UUID, dto: UserUpdateDto) =
        put("/user/$userId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(dto)
        }


    // creates user and checks if it was created
    private suspend fun HttpClient.createUser(dto: UserCreationDto): UserDto {
        val response = post("/user") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(dto)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val user = response.body<UserDto>()
        assertEquals(dto.name, user.name)
        return user
    }
}
