package nd.darty.forms.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nd.darty.forms.model.Form
import java.sql.Connection
import java.sql.Statement

class PostgresFormRepository(private val connection: Connection) : FormRepository {
    companion object {
        private const val CREATE_TABLE_FORMS =
            "CREATE TABLE FORMS (ID SERIAL PRIMARY KEY, NAME VARCHAR(255), DATE VARCHAR(255), OPTIONS VARCHAR(4095));"
        private const val SELECT_FORM_BY_ID = "SELECT name, date, options FROM forms WHERE id = ?"
        private const val INSERT_FORM = "INSERT INTO forms (name, date, options) VALUES (?, ?, ?)"
        private const val UPDATE_FORM = "UPDATE forms SET name = ? WHERE id = ?"
        private const val DELETE_FORM = "DELETE FROM forms WHERE id = ?"
        private const val FETCH_FORMS = "SELECT id, name, date, options FROM forms"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_FORMS)
    }

    override suspend fun allForms(): Collection<Form> {
        val statement = connection.prepareStatement(FETCH_FORMS)
        val resultSet = statement.executeQuery()

        val forms = mutableListOf<Form>()

        while(resultSet.next()) {
            val id = resultSet.getLong("id")
            val name = resultSet.getString("name")
            val date = resultSet.getString("date")
            val options = resultSet.getString("options")

            forms.add(Form(id, name, date, options))
        }
        return forms
    }

    // Create new form
    override suspend fun create(form: Form): Int = withContext(Dispatchers.IO) {
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
    override suspend fun read(id: Long): Form = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_FORM_BY_ID)
        statement.setLong(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            val date = resultSet.getString("date")
            val options = resultSet.getString("options")
            return@withContext Form(id, name, date, options)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a form
    override suspend fun update(id: Long, form: Form) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_FORM)
        statement.setString(1, form.name)
        statement.setLong(2, id)
        statement.executeUpdate()
    }

    // Delete a form
   override suspend fun delete(id: Long): Unit = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_FORM)
        statement.setLong(1, id)
        statement.executeUpdate()
    }
}