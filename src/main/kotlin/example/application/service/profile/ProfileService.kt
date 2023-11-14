package example.application.service.profile

import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class ProfileService(private val profileRepository: ProfileRepository,
                     private val usernameChangeValidationService: UsernameChangeValidationService) {
    /**
     * プロフィールを取得する
     */
    @Transactional(readOnly = true)
    fun findProfile(userSession: UserSession): ProfileDto {
        val profile = findProfileOrElseThrowException(userSession)
        return ProfileDto.from(profile)
    }

    /**
     * ユーザー名を変更する
     */
    fun changeUsername(command: UsernameEditCommand, userSession: UserSession) {
        val profile = findProfileOrElseThrowException(userSession)
        if (command.validatedUsername == profile.username) return

        usernameChangeValidationService.requireUsernameChangePossible(command.validatedUsername)

        profile.changeUsername(command.validatedUsername)
        profileRepository.save(profile)
    }

    private fun findProfileOrElseThrowException(userSession: UserSession): Profile {
        return profileRepository.findByAccountId(userSession.accountId)
               ?: throw ProfileNotFoundException(userSession.accountId)
    }
}