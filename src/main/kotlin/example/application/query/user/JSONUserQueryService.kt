package example.application.query.user

import example.application.shared.usersession.*

interface JSONUserQueryService {
    /**
     * ユーザーを取得する
     */
    fun findUser(userSession: UserSession): JSONUser

    /**
     * メンバーユーザー一覧を取得する
     */
    fun findMemberJSONUsers(userSession: UserSession): JSONUsers

    /**
     * キーワードでユーザー一覧を取得する
     */
    fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers
}