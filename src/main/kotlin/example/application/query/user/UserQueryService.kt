package example.application.query.user

import example.application.shared.usersession.*
import example.domain.model.account.*

interface UserQueryService {
    /**
     * メンバーユーザーを取得する
     */
    fun findMemberUser(accountId: AccountId, userSession: UserSession): User
}