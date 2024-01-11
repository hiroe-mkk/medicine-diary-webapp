package example.infrastructure.emailsender.shared

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(val name: String,
                                 val endpoint: Endpoint,
                                 val emailAddress: String,
                                 val admin: Admin) {

    data class Endpoint(val web: String,
                        val image: String)

    data class Admin(val username: String,
                     val password: String)
}