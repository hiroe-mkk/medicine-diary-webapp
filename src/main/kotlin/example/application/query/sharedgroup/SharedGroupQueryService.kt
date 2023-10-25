package example.application.query.sharedgroup

import example.application.shared.usersession.*

interface SharedGroupQueryService {
    /**
     * 共有グループ詳細一覧を取得する
     */
    fun findSharedGroupDetails(userSession: UserSession): SharedGroups
}