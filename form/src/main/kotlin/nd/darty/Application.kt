package nd.darty

import io.ktor.server.application.*
import nd.darty.database.connectToPostgres
import nd.darty.forms.repository.PostgresFormRepository
import nd.darty.forms.routes.formRoutes
import nd.darty.plugins.configureHTTP
import nd.darty.plugins.configureRouting
import nd.darty.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val connection = connectToPostgres(true)
    val formRepository = PostgresFormRepository(connection)

    configureHTTP()
    configureSerialization()
    configureRouting()

    formRoutes(formRepository)
}
