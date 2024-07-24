package example.infrastructure.emailsender.shared.javamail

import example.infrastructure.emailsender.shared.*
import jakarta.mail.internet.*
import org.slf4j.*
import org.springframework.context.annotation.*
import org.springframework.mail.javamail.*
import org.springframework.stereotype.*
import java.nio.charset.*

@Profile("dev-local", "dev-container")
@Component
class JavaMailEmailSenderClient(private val javaMailSender: JavaMailSender) : EmailSenderClient {
    private val logger = LoggerFactory.getLogger(JavaMailEmailSenderClient::class.java)

    override fun send(email: Email) {
        val mimeMessage = javaMailSender.createMimeMessage()

        val startTime = System.currentTimeMillis()
        try {
            configureMimeMessage(mimeMessage, email)
            javaMailSender.send(mimeMessage)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Email sent via JavaMail. (From: ${email.header.from}, To: ${email.header.to}, Subject: '${email.header.subject}') $duration ms",
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to send email via JavaMail. (From: ${email.header.from}, To: ${email.header.to}, Subject: '${email.header.subject}') Error: ${exception.message}",
            )
            throw EmailSendException(email, exception)
        }
    }

    private fun configureMimeMessage(mimeMessage: MimeMessage, email: Email) {
        val helper = MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name())
        helper.setFrom(email.header.from)
        helper.setTo(email.header.to)
        helper.setSubject(email.header.subject)
        helper.setText(email.body)
    }
}
