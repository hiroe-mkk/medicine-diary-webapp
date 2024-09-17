package example.application.query.user

import example.application.shared.usersession.*
import example.domain.model.account.*

interface UserQueryService {
    /**
     * ユーザーを取得する
     */
    fun getUser(accountId: AccountId, userSession: UserSession): User
}
