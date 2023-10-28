package example.application.query.user

import example.application.query.shared.type.*
import example.application.shared.usersession.*

interface UserQueryService {
    /**
     * ユーザー検索
     */
    fun findByKeyword(keyword: String, userSession: UserSession): List<User>
}