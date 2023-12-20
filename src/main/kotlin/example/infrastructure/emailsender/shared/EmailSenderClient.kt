package example.infrastructure.emailsender.shared

interface EmailSenderClient {
    fun send(email: Email)
}