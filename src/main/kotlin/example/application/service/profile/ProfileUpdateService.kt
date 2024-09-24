package example.application.service.profile

import example.application.service.account.*
import example.application.shared.command.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class ProfileUpdateService(private val profileRepository: ProfileRepository,
                           private val profileImageStorage: ProfileImageStorage) {
    /**
     * ユーザー名を変更する
     */
    fun changeUsername(command: UsernameEditCommand, userSession: UserSession) {
        val profile = profileRepository.findByAccountId(userSession.accountId)
                      ?: throw AccountNotFoundException(userSession.accountId)
        if (command.validatedUsername == profile.username) return

        profile.changeUsername(command.validatedUsername)
        profileRepository.save(profile)
    }

    /**
     * プロフィール画像を変更する
     */
    fun changeProfileImage(command: ImageUploadCommand, userSession: UserSession): ProfileImageURL {
        val profile = profileRepository.findByAccountId(userSession.accountId)
                      ?: throw AccountNotFoundException(userSession.accountId)

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
}
