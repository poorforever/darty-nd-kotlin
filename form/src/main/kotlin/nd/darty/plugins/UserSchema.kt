package nd.darty.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
data class User(val name: String)
class UserService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_USERS =
            "CREATE TABLE USERS (ID SERIAL PRIMARY KEY, NAME VARCHAR(255));"
        private const val SELECT_USER_BY_ID = "SELECT name FROM users WHERE id = ?"
        private const val INSERT_USER = "INSERT INTO useres (name) VALUES (?)"
        private const val UPDATE_USER = "UPDATE users SET name = ? WHERE id = ?"
        private const val DELETE_USER = "DELETE FROM users WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USERS)
    }

    private var newCityId = 0

    // Create new user
    suspend fun create(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.name)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted city")
        }
    }

    // Read a user
    suspend fun read(id: Int): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            return@withContext User(name)
        } else {
            throw Exception("Record not found")
        }
    }

    // Update a user
    suspend fun update(id: Int, user: User) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER)
        statement.setString(1, user.name)
        statement.executeUpdate()
    }

    // Delete a user
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}