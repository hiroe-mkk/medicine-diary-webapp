package example.infrastructure.email.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.sharedgroup.*
import example.infrastructure.email.shared.*
import example.infrastructure.shared.*
import org.springframework.stereotype.*

@Component
class SharedGroupInviteEmailSenderImpl(private val applicationProperties: ApplicationProperties,
                                       private val emailSenderClient: EmailSenderClient)
    : SharedGroupInviteEmailSender {
    override fun send(sharedGroupInviteForm: SharedGroupInviteForm) {
        val subject = if (sharedGroupInviteForm.inviter != null) {
            "【${applicationProperties.name}】 ${sharedGroupInviteForm.inviter} さん から共有グループに招待されました"
        } else {
            "【${applicationProperties.name}】 共有グループに招待されました"
        }
        val header = Email.Header(applicationProperties.emailAddress.contact.sender,
                                  sharedGroupInviteForm.emailAddress.toString(),
                                  subject)

        val body = buildString {
            val title = if (sharedGroupInviteForm.inviter != null) {
                "${sharedGroupInviteForm.inviter} さん から共有グループに招待されています。\n\n"
            } else {
                "共有グループに招待されています。\n\n"
            }
            append(title)

            append("以下のリンクから、共有グループに参加してください。\n")
            append("${sharedGroupInviteForm.inviteLink}\n\n")

            append("※共有グループに参加するには、ログインが必要です。\n")
            append("※このリンクは、発行日から${sharedGroupInviteForm.expirationPeriodText}有効です。\n\n")

            append("--------------------------------------------------\n\n")

            append("本メールにお心当たりがない場合は、恐れ入りますが、削除していただけますようお願いいたします。\n")
            append("このメールは自動送信されております。ご不明な点がございましたら、お問い合わせフォームからご連絡ください。\n\n")

            append("■ サイトトップページ：${applicationProperties.endpoint.web}\n")
            append("■ お問い合わせフォーム：${applicationProperties.endpoint.web}/contact\n\n")

            append("配信元：${applicationProperties.name}\n")
        }

        try {
            emailSenderClient.send(Email(header, body))
        } catch (exception: EmailSendException) {
            throw SharedGroupInviteEmailSendFailedException(sharedGroupInviteForm, exception)
        }
    }
}
