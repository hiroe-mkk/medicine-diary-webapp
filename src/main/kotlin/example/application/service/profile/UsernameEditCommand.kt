package example.application.service.profile

import example.domain.model.account.profile.*
import jakarta.validation.constraints.*

/**
 * ユーザー名の変更に利用される Command クラス
 */
data class UsernameEditCommand(@field:Size(max = 30,
                                           message = "※{max}文字以内で入力してください。")
                               val username: String) {
    val validatedUsername: Username = Username(username.trim())
}