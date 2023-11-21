package example.application.query.sharedgroup

import example.application.shared.usersession.*

interface JSONSharedGroupQueryService {
    /**
     * 共有グループ一覧を取得する
     */
    fun findJSONSharedGroup(userSession: UserSession): JSONSharedGroups
}