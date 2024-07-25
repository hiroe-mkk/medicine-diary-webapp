package example.infrastructure.email.shared

interface EmailSenderClient {
    fun send(email: Email)
}
