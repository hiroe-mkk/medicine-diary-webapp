package example.application.query.user

import example.application.shared.usersession.*

interface JSONUserQueryService {
    /**
     * ユーザーを取得する
     */
    fun findJSONUser(userSession: UserSession): JSONUser

    /**
     * メンバーユーザー一覧を取得する
     */
    fun findJSONMemberUsers(userSession: UserSession): JSONUsers

    /**
     * キーワードでユーザー一覧を取得する
     */
    fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers
}