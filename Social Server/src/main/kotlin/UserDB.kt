import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object Database {
    private const val DB_URL = "jdbc:sqlite:ktor_db.sqlite"
    private val conn: Connection = DriverManager.getConnection(DB_URL)

    init {
        try {
            conn.createStatement().use { stmt ->
                // Create users table
                stmt.executeUpdate(
                    """CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL
                    )"""
                )

                // Create bitmaps table
                stmt.executeUpdate(
                    """CREATE TABLE IF NOT EXISTS bitmaps (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        image BLOB NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )"""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Ensures a single connection is used and synchronized
    private fun <T> useConnection(block: (Connection) -> T): T {
        return synchronized(conn) {
            block(conn)
        }
    }

    fun getUserByUsername(username: String): ResultSet? {
        return useConnection { conn ->
            val stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")
            stmt.setString(1, username)
            stmt.executeQuery()
        }
    }

    fun insertUser(username: String, password: String): Boolean {
        return useConnection { conn ->
            val stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")
            stmt.setString(1, username)
            stmt.setString(2, password)
            stmt.executeUpdate() > 0
        }
    }

    fun insertBitmap(userId: Int, imageData: ByteArray): Boolean {
        return useConnection { conn ->
            val stmt = conn.prepareStatement("INSERT INTO bitmaps (user_id, image) VALUES (?, ?)")
            stmt.setInt(1, userId)
            stmt.setBytes(2, imageData)
            stmt.executeUpdate() > 0
        }
    }

    fun getBitmaps(): ResultSet {
        return useConnection { conn ->
            val stmt = conn.createStatement()
            stmt.executeQuery("SELECT * FROM bitmaps")
        }
    }
}
