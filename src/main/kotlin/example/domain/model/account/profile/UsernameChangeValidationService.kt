package example.domain.model.account.profile

import org.springframework.stereotype.*

@Component
class UsernameChangeValidationService(private val profileRepository: ProfileRepository) {
    fun requireUsernameChangePossible(username: Username) {
        if (!canUsernameChange(username)) throw UsernameChangeException(username, "無効なユーザー名が指定されました。")
    }

    fun canUsernameChange(username: Username): Boolean {
        return profileRepository.findByUsername(username) == null && !username.isDefaultValue()
    }
}