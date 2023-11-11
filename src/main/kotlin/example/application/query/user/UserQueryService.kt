package example.application.query.user

import example.application.query.shared.type.*
import example.application.shared.usersession.*

interface UserQueryService {
    /**
     * キーワードでユーザー一覧を取得する
     */
    fun findByKeyword(keyword: String, userSession: UserSession): List<User>

    /**
     * メンバーユーザー一覧を取得する
     */
    fun findMemberUsers(userSession: UserSession): List<User>
}