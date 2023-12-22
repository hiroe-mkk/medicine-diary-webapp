package example.application.service.contact

import example.domain.model.contact.*
import org.springframework.stereotype.*

@Service
class ContactService(private val contactEmailSender: ContactEmailSender) {
    /**
     * お問い合わせフォームを送信する
     */
    fun sendContactForm(command: ContactFormCreationCommand) {
        contactEmailSender.send(ContactForm(command.validatedEmailAddress,
                                            command.validatedName,
                                            command.validatedContent))
    }
}