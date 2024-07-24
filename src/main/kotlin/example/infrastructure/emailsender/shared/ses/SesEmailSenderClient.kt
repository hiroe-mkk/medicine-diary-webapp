package example.infrastructure.emailsender.shared.ses

import com.amazonaws.services.simpleemail.*
import com.amazonaws.services.simpleemail.model.*
import example.infrastructure.emailsender.shared.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*


@Profile("prod")
@Component
class SesEmailSenderClient(private val amazonSimpleEmailService: AmazonSimpleEmailService) : EmailSenderClient {
    override fun send(email: Email) {
        val request: SendEmailRequest = SendEmailRequest()
            .withDestination(Destination().withToAddresses(email.header.to))
            .withMessage(Message()
                             .withBody(Body().withText(Content().withCharset("UTF-8").withData(email.body)))
                             .withSubject(Content().withCharset("UTF-8").withData(email.header.subject)))
            .withSource(email.header.from)
        amazonSimpleEmailService.sendEmail(request)
    }
}
