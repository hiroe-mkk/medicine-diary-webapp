package example.domain.model.account.profile

import org.springframework.stereotype.*

@Component
class ProfileDomainService(private val profileRepository: ProfileRepository) {
    fun requireUsernameChangeableState(username: Username) {
        if (profileRepository.findByUsername(username) != null) throw DuplicateUsernameException(username)
    }
}