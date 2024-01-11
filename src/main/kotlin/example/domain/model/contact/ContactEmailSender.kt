package example.domain.model.contact

interface ContactEmailSender {
    fun send(contactForm: ContactForm)
}