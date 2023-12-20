package example.infrastructure.emailsender.shared.javamail

import example.infrastructure.emailsender.shared.*
import org.springframework.context.annotation.*
import org.springframework.mail.*
import org.springframework.mail.javamail.*
import org.springframework.stereotype.*
import java.nio.charset.*

@Profile("local")
@Component
class JavaMailEmailSenderClient(private val javaMailSender: JavaMailSender) : EmailSenderClient {
    override fun send(email: Email) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name())
        helper.setFrom(email.header.from.toString())
        helper.setTo(email.header.to.toString())
        helper.setSubject(email.header.subject)
        helper.setText(email.body, true)

        javaMailSender.send(mimeMessage) // TODO: MailExceptionをラップするか検討する
    }
}