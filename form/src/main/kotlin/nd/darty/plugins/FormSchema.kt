package nd.darty.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
data class Form(val name: String, val date: String, val options: String)
class FormService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_FORMS =
            "CREATE TABLE FORMS (ID SERIAL PRIMARY KEY, NAME VARCHAR(255), DATE VARCHAR(255), OPTIONS VARCHAR(4095));"
        private const val SELECT_FORM_BY_ID = "SELECT name, date, options FROM forms WHERE id = ?"
        private const val INSERT_FORM = "INSERT INTO forms (name, date, options) VALUES (?, ?, ?)"
        private const val UPDATE_FORM = "UPDATE forms SET name = ? WHERE id = ?"
        private const val DELETE_FORM = "DELETE FROM forms WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_FORMS)
    }

    private var newCityId = 0

    // Create new form
    suspend fun create(form: Form): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_FORM, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, form.name)
        statement.setString(2, form.date)
        statement.setString(3, form.options)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted city")
        }
    }

    // Read a form
    suspend fun read(id: Int): Form = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_FORM_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            val date = resultSet.getString("date")
            val options = resultSet.getString("options")
            return@withContext Form(name, date, options)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a form
    suspend fun update(id: Int, form: Form) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_FORM)
        statement.setString(1, form.name)
        statement.setInt(2, id)
        statement.executeUpdate()
    }

    // Delete a form
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_FORM)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}