package example.application.query.user

import example.application.shared.usersession.*
import example.domain.model.account.*

interface UserQueryService {
    /**
     * 共有グループメンバーを取得する
     */
    fun findSharedGroupMember(accountId: AccountId, userSession: UserSession): User
}
