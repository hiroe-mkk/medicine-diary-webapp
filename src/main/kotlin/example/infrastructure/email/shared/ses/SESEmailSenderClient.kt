package example.infrastructure.email.shared.ses

import com.amazonaws.services.simpleemail.*
import com.amazonaws.services.simpleemail.model.*
import example.infrastructure.email.shared.*
import org.slf4j.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*


@Profile("prod")
@Component
class SESEmailSenderClient(private val amazonSimpleEmailService: AmazonSimpleEmailService) : EmailSenderClient {
    private val logger = LoggerFactory.getLogger(SESEmailSenderClient::class.java)

    override fun send(email: Email) {

        val startTime = System.currentTimeMillis()
        try {
            val request: SendEmailRequest = SendEmailRequest()
                .withDestination(Destination().withToAddresses(email.header.to))
                .withMessage(Message()
                                 .withBody(Body().withText(Content().withCharset("UTF-8").withData(email.body)))
                                 .withSubject(Content().withCharset("UTF-8").withData(email.header.subject)))
                .withSource(email.header.from)
            amazonSimpleEmailService.sendEmail(request)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Email sent via AWS SES. (From: ${email.header.from}, To: ${email.header.to}, Subject: '${email.header.subject}') $duration ms",
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to send email via AWS SES. (From: ${email.header.from}, To: ${email.header.to}, Subject: '${email.header.subject}') Error: ${exception.message}",
            )
            throw EmailSendException(email, exception)
        }
    }
}
