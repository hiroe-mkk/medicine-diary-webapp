package example.application.service.contact

import example.domain.model.contact.*
import org.springframework.stereotype.*

@Service
class ContactService(private val contactEmailSender: ContactEmailSender) {
    /**
     * お問い合わせメールを送信する
     */
    fun sendContactEmail(command: ContactFormCreationCommand) {
        contactEmailSender.send(ContactForm(command.validatedEmailAddress,
                                            command.validatedName,
                                            command.validatedMessage))
    }
}