package dev.forst.idt.routing

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.path.normal.put
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import dev.forst.idt.dto.UserCreationDto
import dev.forst.idt.dto.UserDto
import dev.forst.idt.dto.UserUpdateDto
import dev.forst.idt.error.UserNotFoundException
import dev.forst.idt.service.UsersService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.UUID

/**
 * Install typed routing.
 */
fun Application.installRouting() {
    routing {
        // convenient redirect to swagger UI
        get("/") {
            call.respondRedirect("/swagger-ui/index.html")
        }
    }

    // and now example routing
    apiRouting {
        // we lazy obtain instance of UsersService from the DI container
        val usersService by ktorRoute.closestDI().instance<UsersService>()

        route("user") {
            route("{id}") {
                // now we indicate that if endpoints throw UserNotFoundException, we return 404
                // this can be done separately from routing, but it's easier do it here for such small project
                throws(HttpStatusCode.NotFound, gen = { ex: UserNotFoundException -> ErrorResponse(ex.message) }) {
                    // GET /user/{id}
                    get<UserIdParam, UserDto>(
                        info(summary = "Returns user entity by ID.")
                    ) { (id) ->
                        val user = usersService.getUser(id) ?: throw UserNotFoundException(id)
                        respond(user)
                    }
                    // PUT /user/{id}
                    put<UserIdParam, UserDto, UserUpdateDto>(
                        info(
                            summary = "Applies given change-set to a user with given ID.",
                            description = "Client is expected to provide change set (only data that changed) and not whole entity."
                        ),
                    ) { (id), userUpdate ->
                        val user = usersService.updateUser(id, userUpdate) ?: throw UserNotFoundException(id)
                        respond(user)
                    }
                }
            }
            // not part of the assignment, but we want to have POST as well
            // POST /user
            post<Unit, UserDto, UserCreationDto>(
                info("Creates new user.")
            ) { _, creation ->
                respond(usersService.createUser(creation))
            }
        }

    }
}

internal data class UserIdParam(@PathParam("ID of the user") val id: UUID)
internal data class ErrorResponse(val message: String)
