package example.application.service.profileimage

import example.application.service.account.*
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
    fun changeProfileImage(command: ImageUploadCommand, userSession: UserSession): ProfileImageURL {
        val profile = findProfileOrElseThrowException(userSession)

        profile.profileImageURL?.let { profileImageStorage.delete(it) }
        val profileImageURL = profileImageStorage.createURL()
        profile.changeProfileImage(profileImageURL)
        profileRepository.save(profile)
        profileImageStorage.upload(profileImageURL, command.validatedFileContent())

        return profileImageURL
    }

    /**
     * プロフィール画像を削除する
     */
    fun deleteProfileImage(userSession: UserSession) {
        val profile = profileRepository.findByAccountId(userSession.accountId)
        if (profile?.profileImageURL == null) return

        profileImageStorage.delete(profile.profileImageURL!!)
        profile.deleteProfileImage()
        profileRepository.save(profile)
    }

    private fun findProfileOrElseThrowException(userSession: UserSession): Profile {
        // プロフィールのライフサイクルはアカウントと同じため、プロフィールが見つからない場合は、アカウントが存在しないと考えられる
        return profileRepository.findByAccountId(userSession.accountId)
               ?: throw AccountNotFoundException(userSession.accountId)
    }
}