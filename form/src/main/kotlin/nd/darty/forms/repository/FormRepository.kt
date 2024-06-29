package nd.darty.forms.repository

import nd.darty.forms.model.Form

interface FormRepository {
    suspend fun allForms(): Collection<Form>
    suspend fun create(form: Form): Int
    suspend fun update(id: Long, form: Form): Int
    suspend fun delete(id: Long)
    suspend fun read(id: Long): Form
}