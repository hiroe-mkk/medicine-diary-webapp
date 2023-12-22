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
            append(htmlEmailTemplateFactory.createBodyHeader("お問い合わせ受け付け完了のお知らせ"))
            val lines = listOf("この度は、お問い合わせいただき、誠にありがとうございます。",
                               "下記の通りお問い合わせを受け付けました。",
                               "--------------------------------------------------",
                               "◆お名前",
                               contactForm.name,
                               "◆ご連絡先メールアドレス",
                               contactForm.emailAddress.value,
                               "◆お問い合わせ内容",
                               contactForm.content,
                               "--------------------------------------------------",
                               "お問い合わせいただいた内容につきましては、確認次第返信させていただきますので、今しばらくお待ちください。",
                               "お問い合わせ内容に対する回答や追加情報は、異なるアドレスから送信される場合がありますので、ご了承ください。",
                               "なお、返信がない場合は、送信トラブルの可能性がございます。お手数ですが再度お問い合わせいただけますと幸いです。")
            append(htmlEmailTemplateFactory.createBodyMain(lines))
            append(htmlEmailTemplateFactory.createBodyFooter())
        }
    }
}