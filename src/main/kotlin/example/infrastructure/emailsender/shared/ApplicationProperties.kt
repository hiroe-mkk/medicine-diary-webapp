package example.infrastructure.emailsender.shared

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(val name: String,
                                 val endpoint: Endpoint,
                                 val emailAddress: EmailAddress,
                                 val admin: Admin) {

    data class Endpoint(val web: String,
                        val image: String)

    data class EmailAddress(val contact: String)

    data class Admin(val username: String,
                     val password: String)
}