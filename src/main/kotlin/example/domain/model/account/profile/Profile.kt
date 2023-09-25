package example.domain.model.account.profile

import example.domain.model.account.*

/**
 * プロフィール
 */
class Profile(val accountId: AccountId, // TODO: ProfileId クラスを定義するべきか再考する
              val username: Username)