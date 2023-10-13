package example.application.service.profileimage

import example.application.service.profile.*
import example.application.shared.command.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class ProfileImageService(private val profileRepository: ProfileRepository,
                          private val profileImageStorage: ProfileImageStorage) {
    /**
     * プロフィール画像を変更する
     */
    fun changeProfileImage(command: ImageUploadCommand,
                           userSession: UserSession): ProfileImageURL {
        val profile = findProfileOrElseThrowException(userSession)

        profile.profileImageURL?.let { profileImageStorage.delete(it) }

        val profileImageURL = profileImageStorage.createURL()
        profile.changeProfileImage(profileImageURL)
        profileRepository.save(profile)
        profileImageStorage.upload(profileImageURL, command.validatedFileContent())

        return profileImageURL
    }

    private fun findProfileOrElseThrowException(userSession: UserSession): Profile {
        return profileRepository.findByAccountId(userSession.accountId)
               ?: throw ProfileNotFoundException(userSession.accountId)
    }
}