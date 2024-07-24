package example.infrastructure.emailsender.shared

data class Email(val header: Header,
                 val body: String) {
    data class Header(val from: String,
                      val to: String,
                      val subject: String)
}
