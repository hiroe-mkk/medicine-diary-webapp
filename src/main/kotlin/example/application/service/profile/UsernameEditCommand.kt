package example.application.service.profile

import example.domain.model.account.profile.*
import example.domain.shared.validation.*
import jakarta.validation.constraints.*

/**
 * ユーザー名の変更に利用される Command クラス
 */
data class UsernameEditCommand(@field:NotWhitespaceOnly(message = "※ユーザー名を入力してください。")
                               @field:Size(max = 30,
                                           message = "※{max}文字以内で入力してください。")
                               @field:UnregisteredUsername
                               val username: String) {
    val validatedUsername: Username = Username(username.trim())
}