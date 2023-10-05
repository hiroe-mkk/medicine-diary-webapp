package example.application.service.profile

import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class ProfileService(private val profileRepository: ProfileRepository,
                     private val profileImageStorage: ProfileImageStorage) {
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

    /**
     * プロフィール画像を変更する
     */
    fun changeProfileImage(command: ProfileImageChangeCommand,
                           userSession: UserSession): ProfileImageURL {
        val profile = findProfileOrElseThrowException(userSession)

        profile.profileImageURL?.let { profileImageStorage.delete(it) }

        val profileImageURL = profileImageStorage.createURL()
        profile.changeProfileImage(profileImageURL)
        profileRepository.save(profile)

        val profileImage = ProfileImage(profileImageURL, command.validatedFileContent())
        profileImageStorage.upload(profileImage)

        return profileImageURL
    }

    private fun findProfileOrElseThrowException(userSession: UserSession): Profile {
        return profileRepository.findByAccountId(userSession.accountId)
               ?: throw AccountNotFoundException(userSession.accountId)
    }
}