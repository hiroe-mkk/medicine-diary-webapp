package example.infrastructure.email.contact

import example.domain.model.contact.*
import example.infrastructure.email.shared.*
import example.infrastructure.shared.*
import org.springframework.stereotype.*

@Component
class ContactEmailSenderImpl(private val applicationProperties: ApplicationProperties,
                             private val emailSenderClient: EmailSenderClient) : ContactEmailSender {
    override fun send(contactForm: ContactForm) {
        sendToAdmin(contactForm)
        sendToClient(contactForm)
    }

    private fun sendToAdmin(contactForm: ContactForm) {
        val header = Email.Header(applicationProperties.emailAddress.contact.sender,
                                  applicationProperties.emailAddress.contact.handler,
                                  "お問い合わせを受け付けました")
        val body = buildString {
            append("下記の通りお問い合わせを受け付けましたので、対応をお願いします。\n\n\n")

            append("--------------------------------------------------\n")
            append("◆お名前\n")
            append("${contactForm.name} 様\n\n")
            append("◆ご連絡先メールアドレス\n")
            append("${contactForm.emailAddress.value}\n\n")
            append("◆お問い合わせ内容\n")
            append("${contactForm.content}\n")
            append("--------------------------------------------------")
        }

        try {
            emailSenderClient.send(Email(header, body))
        } catch (exception: EmailSendException) {
            throw ContactFailedException(contactForm, exception)
        }
    }

    private fun sendToClient(contactForm: ContactForm) {
        val header = Email.Header(applicationProperties.emailAddress.contact.sender,
                                  contactForm.emailAddress.toString(),
                                  "【${applicationProperties.name}】 お問い合わせ受け付け完了のお知らせ")
        val body = buildString {
            append("この度は、お問い合わせいただき、誠にありがとうございます。\n")
            append("下記の通りお問い合わせを受け付けました。\n\n")

            append("--------------------------------------------------\n")
            append("◆お名前\n")
            append("${contactForm.name} 様\n\n")
            append("◆ご連絡先メールアドレス\n")
            append("${contactForm.emailAddress.value}\n\n")
            append("◆お問い合わせ内容\n")
            append("${contactForm.content}\n")
            append("--------------------------------------------------\n\n")

            append("お問い合わせいただいた内容につきましては、確認次第返信させていただきますので、今しばらくお待ちください。\n")
            append("お問い合わせ内容に対する回答や追加情報は、異なるアドレスから送信される場合がありますので、ご了承ください。\n")
            append("なお、返信がない場合は、送信トラブルの可能性がございます。お手数ですが再度お問い合わせいただけますと幸いです。\n\n\n\n")

            append("--------------------------------------------------\n")
            append("■ ご注意\n")
            append("※本メールにお心当たりがない場合は、誠に恐れ入りますが、破棄していただけますようお願い申し上げます。\n\n")

            append("■ 配信：${applicationProperties.name}\n")
            append(applicationProperties.endpoint.web)
        }

        try {
            emailSenderClient.send(Email(header, body))
        } catch (exception: EmailSendException) {
            throw ContactFailedException(contactForm, exception)
        }
    }
}
