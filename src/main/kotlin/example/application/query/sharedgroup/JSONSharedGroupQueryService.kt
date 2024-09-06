package example.application.query.sharedgroup

import example.application.shared.usersession.*

interface JSONSharedGroupQueryService {
    /**
     * 参加している共有グループを取得する
     */
    fun findJSONJoinedSharedGroup(userSession: UserSession): JSONSharedGroup
}
