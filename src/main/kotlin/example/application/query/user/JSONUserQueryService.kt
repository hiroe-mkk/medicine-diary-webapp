package example.application.query.user

import example.application.shared.usersession.*
import example.domain.model.account.*

interface JSONUserQueryService {
    /**
     * ユーザーを取得する
     */
    fun findJSONUser(accountId: AccountId): JSONUser

    /**
     * メンバーユーザー一覧を取得する
     */
    fun findJSONMemberUsers(userSession: UserSession): JSONUsers

    /**
     * キーワードでユーザー一覧を取得する
     */
    fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers
}