package example.application.query.user

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

interface JSONUserQueryService {
    /**
     * ユーザーを取得する
     */
    fun findJSONUser(accountId: AccountId): JSONUser

    /**
     * 共有グループのメンバー一覧を取得する
     */
    fun findJSONSharedGroupMember(sharedGroupId: SharedGroupId): JSONUsers
}
