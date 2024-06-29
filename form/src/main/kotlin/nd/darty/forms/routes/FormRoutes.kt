package nd.darty.forms.routes

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import nd.darty.forms.model.Form
import nd.darty.forms.repository.FormRepository

fun Application.formRoutes(formRepository: FormRepository) {
    routing {
     route("/forms") {
         get {
             val forms = formRepository.allForms()
             call.respond(OK, forms)
         }

         post {
             val form = call.receive<Form>()
             call.respond(OK, formRepository.create(form))
         }

         delete("/{id}") {
             val id = call.parameters["id"]?.toLongOrNull() ?: throw NotFoundException()
             call.respond(OK, formRepository.delete(id))
         }
     }
    }
}