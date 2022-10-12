package dev.forst.idt.routing

import com.papsign.ktor.openapigen.OpenAPIGen
import dev.forst.idt.defaultDiModule
import io.ktor.client.HttpClient
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.testing.ApplicationTestBuilder
import org.kodein.di.DI
import org.kodein.di.ktor.di
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

abstract class TestBase {
    /**
     * Sets up an application in a test mode.
     */
    protected fun ApplicationTestBuilder.setupApplication(
        di: (DI.MainBuilder.() -> Unit)? = null
    ) = application {
        if (di != null) {
            di { di() }
        } else {
            defaultDiModule()
        }
        install(ServerContentNegotiation) { jackson() }
        install(OpenAPIGen)
        installRouting()
    }

    /**
     * Creates test client.
     */
    protected fun ApplicationTestBuilder.client() = createClient {
        install(ClientContentNegotiation) {
            jackson()
        }
    }

    /**
     * Invokes [setupApplication] and then [client] in one function.
     */
    protected fun ApplicationTestBuilder.prepareApplication(
        di: (DI.MainBuilder.() -> Unit)? = null
    ): HttpClient {
        setupApplication(di)
        return client()
    }
}
