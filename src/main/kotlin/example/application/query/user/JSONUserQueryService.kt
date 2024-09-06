package example.application.query.user

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*

interface JSONUserQueryService {
    /**
     * ユーザーを取得する
     */
    fun findJSONUser(accountId: AccountId): JSONUser

    /**
     * キーワードでユーザー一覧を取得する
     */
    fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers

    /**
     * 共有グループのメンバー一覧を取得する
     */
    fun findJSONSharedGroupMember(sharedGroupId: SharedGroupId, userSession: UserSession): JSONUsers
}
