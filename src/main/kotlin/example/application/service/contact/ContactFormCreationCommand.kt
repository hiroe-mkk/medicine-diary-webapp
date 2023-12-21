package example.application.service.contact

import example.domain.shared.type.*
import example.domain.shared.validation.*
import jakarta.validation.constraints.*

/**
 * お問い合わせフォームの作成に利用される Command クラス
 */
data class ContactFormCreationCommand(@field:NotWhitespaceOnly(message = "※ご連絡先メールアドレスを入力してください。")
                                      @field:Email(message = "※正しい形式で入力してください。")
                                      val emailAddress: String,
                                      @field:NotWhitespaceOnly(message = "※お名前を入力してください。")
                                      @field:Size(max = 30, message = "※{max}文字以内で入力してください。")
                                      val name: String,
                                      @field:NotWhitespaceOnly(message = "※お問い合わせ内容を入力してください。")
                                      @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                      val message: String) {
    val validatedEmailAddress: EmailAddress = EmailAddress(emailAddress.trim())
    val validatedName: String = name.trim()
    val validatedMessage: String = message.trim()

    companion object {
        fun initialize(): ContactFormCreationCommand {
            return ContactFormCreationCommand("", "", "")
        }
    }
}