package example.application.service.profile

import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.profile.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class ProfileService(private val profileRepository: ProfileRepository) {
    /**
     * プロフィールを取得する
     */
    fun findProfile(userSession: UserSession): ProfileDto {
        val profile = findProfileOrElseThrowException(userSession)
        return ProfileDto.from(profile)
    }

    /**
     * ユーザー名を変更する
     */
    fun changeUsername(command: UsernameChangeCommand, userSession: UserSession) {
        val profile = findProfileOrElseThrowException(userSession)
        profile.changeUsername(command.validatedUsername)
        profileRepository.save(profile)
    }

    private fun findProfileOrElseThrowException(userSession: UserSession): Profile {
        return profileRepository.findByAccountId(userSession.accountId)
               ?: throw AccountNotFoundException(userSession.accountId)
    }
}