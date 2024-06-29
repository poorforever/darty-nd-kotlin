package nd.darty.forms.model

import kotlinx.serialization.Serializable

@Serializable
data class Form(val id: Long, val name: String, val date: String, val options: String)