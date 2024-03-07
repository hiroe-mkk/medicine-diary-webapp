package example.infrastructure.emailsender.shared.javamail

import example.infrastructure.emailsender.shared.*
import org.springframework.context.annotation.*
import org.springframework.mail.javamail.*
import org.springframework.stereotype.*
import java.nio.charset.*

@Profile("dev-local", "dev-container")
@Component
class JavaMailEmailSenderClient(private val applicationProperties: ApplicationProperties,
                                private val javaMailSender: JavaMailSender) : EmailSenderClient {
    override fun send(email: Email) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name())
        helper.setFrom(applicationProperties.emailAddress)
        helper.setTo(email.header.to.toString())
        helper.setSubject(email.header.subject)
        helper.setText(email.body)

        javaMailSender.send(mimeMessage)
    }
}