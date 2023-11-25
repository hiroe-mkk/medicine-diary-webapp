package example.domain.model.account.profile

import example.domain.model.account.*
import example.domain.model.account.profile.profileimage.*

/**
 * プロフィール
 */
class Profile private constructor(val accountId: AccountId, // TODO: ProfileId クラスを定義するべきか再考する
                                  username: Username,
                                  profileImageURL: ProfileImageURL?) {
    var username: Username = username
        private set

    var profileImageURL: ProfileImageURL? = profileImageURL
        private set

    companion object {
        fun create(accountId: AccountId): Profile = Profile(accountId, Username.creteDefaultUsername(), null)

        fun reconstruct(accountId: AccountId,
                        username: Username,
                        profileImageURL: ProfileImageURL?): Profile {
            return Profile(accountId, username, profileImageURL)
        }
    }

    fun changeUsername(newUsername: Username) {
        this.username = newUsername
    }

    fun changeProfileImage(newProfileURL: ProfileImageURL) {
        this.profileImageURL = newProfileURL
    }

    fun deleteProfileImage() {
        this.profileImageURL = null
    }
}