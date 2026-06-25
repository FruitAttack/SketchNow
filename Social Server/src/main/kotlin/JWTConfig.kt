import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JWTConfig {
    private const val secret = "super_secret_key" // Change to a secure, stored secret!
    private const val issuer = "ktor.io"
    private const val validityInMs = 36_000_00 * 24 // 24 hours

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun createToken(username: String): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("username", username)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}
