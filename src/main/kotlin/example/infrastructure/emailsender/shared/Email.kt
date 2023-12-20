package example.infrastructure.emailsender.shared

import example.domain.shared.type.*

data class Email(val header: Header,
                 val body: String) {
    data class Header(val from: EmailAddress,
                      val to: EmailAddress,
                      val subject: String)
}