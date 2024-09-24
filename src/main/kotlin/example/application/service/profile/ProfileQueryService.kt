package example.application.service.profile

import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional(readOnly = true)
class ProfileQueryService(private val profileRepository: ProfileRepository) {
    /**
     * プロフィールを取得する
     */
    fun getProfile(userSession: UserSession): ProfileDto {
        val profile = profileRepository.findByAccountId(userSession.accountId)
                      ?: throw AccountNotFoundException(userSession.accountId)
        return ProfileDto.from(profile)
    }
}
