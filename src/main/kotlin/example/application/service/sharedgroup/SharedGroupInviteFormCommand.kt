package example.application.service.sharedgroup

import example.domain.shared.type.*
import example.domain.shared.validation.*
import jakarta.validation.constraints.*

/**
 * 共有グループ招待フォームの作成に利用される Command クラス
 */
data class SharedGroupInviteFormCommand(@field:NotWhitespaceOnly(message = "※招待する相手のメールアドレスを入力してください。")
                                        @field:Email(message = "※正しい形式で入力してください。")
                                        val emailAddress: String) {
    val validatedEmailAddress: EmailAddress = EmailAddress(emailAddress.trim())

    companion object {
        fun initialize(): SharedGroupInviteFormCommand {
            return SharedGroupInviteFormCommand("")
        }
    }
}
