package example.application.query.user

import example.domain.model.sharedgroup.*

interface JSONUserQueryService {
    /**
     * 共有グループのメンバー一覧を取得する
     */
    fun getSharedGroupMembers(sharedGroupId: SharedGroupId): JSONUsers
}
