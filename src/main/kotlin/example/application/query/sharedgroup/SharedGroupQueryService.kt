package example.application.query.sharedgroup

import example.application.query.shared.type.*
import example.application.shared.usersession.*

interface SharedGroupQueryService {
    /**
     * 共有グループ詳細一覧を取得する
     */
    fun findSharedGroupDetails(userSession: UserSession): SharedGroups

    /**
     * 参加している共有グループのメンバーを取得する
     */
    fun findParticipatingSharedGroupMembers(userSession: UserSession): Set<User>
}