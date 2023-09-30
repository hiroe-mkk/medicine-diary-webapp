package example.domain.model.account.profile

import example.domain.model.account.*
import example.domain.model.account.profile.profileimage.*

/**
 * プロフィール
 */
class Profile(val accountId: AccountId, // TODO: ProfileId クラスを定義するべきか再考する
              username: Username,
              profileImageFullPath: ProfileImageFullPath?) {
    var username: Username = username
        private set

    var profileImageFullPath: ProfileImageFullPath? = profileImageFullPath
        private set

    fun changeUsername(newUsername: Username) {
        this.username = newUsername
    }
}