package example.infrastructure.db.repository.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

class SharedGroupResultEntity(val sharedGroupId: SharedGroupId) {
    private lateinit var membersForMapping: Set<AccountId>
    val members: Set<AccountId>
        get() = membersForMapping

    private lateinit var pendingInvitationsForMapping: Set<PendingInvitation>
    val pendingInvitations: Set<PendingInvitation>
        get() = pendingInvitationsForMapping

    fun toSharedGroup(): SharedGroup = SharedGroup(sharedGroupId, members, pendingInvitations)
}
