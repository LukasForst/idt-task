package dev.forst.idt

import com.papsign.ktor.openapigen.OpenAPIGen
import dev.forst.idt.routing.installRouting
import dev.forst.idt.service.UsersService
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.kodein.di.bindSingleton
import org.kodein.di.ktor.di

fun main() {
    // we use Ktor with Netty as a webserver
    embeddedServer(Netty, port = 8080, host = "localhost") {
        // first we configure dependency injection
        defaultDiModule()
        // install OpenAPI plugin so we can test the server visually
        install(OpenAPIGen) {
            // this serves OpenAPI definition on /openapi.json
            serveOpenApiJson = true
            // this servers Swagger UI on /swagger-ui/index.html
            serveSwaggerUi = true
            info {
                title = "IDT Challange"
            }
        }
        // install JSON support
        install(ContentNegotiation) {
            // we chose jackson as a serializer
            // that's because it is pretty much industry standard, is performant
            // and highly configurable
            jackson()
        }
        // now we install routing
        installRouting()
    }.start(wait = true)
}

/**
 * Registers DI module to the application.
 */
fun Application.defaultDiModule() {
    di {
        // for bigger projects, we would put this to another file
        bindSingleton { UsersService() }
    }
}
