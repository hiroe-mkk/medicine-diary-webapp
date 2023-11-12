package example.application.query.user

import example.application.shared.usersession.*

interface UserQueryService {
    /**
     * メンバーユーザー一覧を取得する
     */
    fun findMemberJSONUsers(userSession: UserSession): JSONUsers

    /**
     * キーワードでユーザー一覧を取得する
     */
    fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers
}