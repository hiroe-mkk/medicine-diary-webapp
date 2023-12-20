package example.infrastructure.emailsender.contact

import example.domain.model.contact.*
import example.infrastructure.emailsender.shared.*
import org.springframework.stereotype.*

@Component
class ContactEmailSenderImpl(private val emailSenderClient: EmailSenderClient,
                             private val htmlEmailTemplateFactory: HtmlEmailFactory) : ContactEmailSender {
    override fun send(contactForm: ContactForm) {
        val header = htmlEmailTemplateFactory.createHeader(contactForm.emailAddress)
        emailSenderClient.send(Email(header, createBody(contactForm)))
    }

    private fun createBody(contactForm: ContactForm): String {
        return buildString {
            append(htmlEmailTemplateFactory.createBodyHeader("お問い合わせ内容の確認のお願い"))
            val lines = listOf("下記の通りお問い合わせを受け付けました。",
                               "--------------------------------------------------",
                               "◆お名前",
                               contactForm.name,
                               "◆ご連絡先メールアドレス",
                               contactForm.emailAddress.value,
                               "◆お問い合わせ内容",
                               contactForm.message,
                               "--------------------------------------------------",
                               "お問い合わせいただき、ありがとうございました。",
                               "確認後、返信いたします。いましばらくお待ちください。",
                               "なお、返信がない場合は、送信トラブルの可能性がございます。お手数ですが再度お問い合わせいただけますと幸いです。")
            append(htmlEmailTemplateFactory.createBodyMain(lines))
            append(htmlEmailTemplateFactory.createBodyFooter())
        }
    }
}