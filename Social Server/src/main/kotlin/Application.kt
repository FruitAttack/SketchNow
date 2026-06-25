import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.http.content.*
import io.ktor.http.invoke
import io.ktor.utils.io.jvm.javaio.*
import java.io.ByteArrayOutputStream
import java.util.*


fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true; isLenient = true })
        }

        install(StatusPages) {
            exception<Throwable> { call, cause ->
                println("Exception occurred: ${cause.localizedMessage}")
                cause.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
            }
        }

        install(Authentication) {
            jwt {
                realm = "ktor sample app"
                verifier(JWTConfig.verifier)
                validate { credential ->
                    if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }

        routing {
            route("/api") {
                post("/auth") {
                    val authRequest = call.receive<LoginRequest>()
                    println("Login attempt: username='${authRequest.username}'")

                    val userResult = Database.getUserByUsername(authRequest.username)
                    if (userResult?.next() == true) {
                        if (userResult.getString("password") == authRequest.password) {
                            val token = JWTConfig.createToken(authRequest.username)
                            println("Login successful for user '${authRequest.username}'")
                            call.respond(HttpStatusCode.OK, AuthResponse(token))
                        } else {
                            println("Login failed: Incorrect password for user '${authRequest.username}'")
                            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                        }
                    } else {
                        println("Login failed: User '${authRequest.username}' not found")
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                }

                post("/user") {
                    val userRequest = call.receive<LoginRequest>()
                    println("Sign up attempt: username='${userRequest.username}'")

                    val userCreated = Database.insertUser(userRequest.username, userRequest.password)
                    if (userCreated) {
                        println("Sign up successful: user '${userRequest.username}' created")
                        call.respond(HttpStatusCode.Created)
                    } else {
                        println("Sign up failed: user '${userRequest.username}' not created")
                        call.respond(HttpStatusCode.BadRequest, "User creation failed")
                    }
                }



                route("/bitmaps") {
                    authenticate {
                        get {
                            val bitmapsResult = Database.getBitmaps()
                            val images = mutableListOf<String>()
                            while (bitmapsResult.next()) {
                                val imageBytes = bitmapsResult.getBytes("image")
                                images.add(Base64.getEncoder().encodeToString(imageBytes))
                            }
                            call.respond(ByteArrayWrapper(images))
                        }

                        post {
                            val multipart = call.receiveMultipart()
                            var bitmapBytes: ByteArray? = null
                            multipart.forEachPart { part ->
                                if (part is PartData.FileItem && part.contentType?.contentType == "image") {
                                    bitmapBytes = part.streamProvider().readBytes()
                                }
                                part.dispose()
                            }

                            if (bitmapBytes == null) {
                                call.respond(HttpStatusCode.BadRequest, "No image uploaded")
                                return@post
                            }

                            val userId = 1
                            val inserted = Database.insertBitmap(userId, bitmapBytes!!)
                            if (inserted) {
                                println("Bitmap uploaded successfully")
                                call.respond(HttpStatusCode.Created)
                            } else {
                                println("Failed to store bitmap")
                                call.respond(HttpStatusCode.InternalServerError, "Failed to store image")
                            }
                        }
                    }
                }

            }
        }
    }.start(wait = true)
}

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class Note(val message: String, val public: Boolean)

@Serializable
data class ByteArrayWrapper(val images: List<String>)