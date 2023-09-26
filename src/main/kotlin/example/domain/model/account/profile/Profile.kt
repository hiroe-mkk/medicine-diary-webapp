package example.domain.model.account.profile

import example.domain.model.account.*

/**
 * プロフィール
 */
class Profile(val accountId: AccountId, // TODO: ProfileId クラスを定義するべきか再考する
              username: Username) {
    var username: Username = username
        private set

    fun changeUsername(newUsername: Username) {
        this.username = newUsername
    }
}