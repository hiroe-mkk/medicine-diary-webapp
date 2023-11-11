package example.application.query.sharedgroup

import example.application.shared.usersession.*

interface SharedGroupQueryService {
    /**
     * 共有グループ一覧を取得する
     */
    fun findSharedGroup(userSession: UserSession): SharedGroups
}