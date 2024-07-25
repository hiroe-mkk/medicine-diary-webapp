package example.infrastructure.db.repository.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

class SharedGroupResultEntity(val sharedGroupId: SharedGroupId) {
    private lateinit var membersForMapping: Set<AccountId>
    val members: Set<AccountId>
        get() = membersForMapping

    private lateinit var inviteesForMapping: Set<AccountId>
    val invitees: Set<AccountId>
        get() = inviteesForMapping

    fun toSharedGroup(): SharedGroup = SharedGroup(sharedGroupId, members, invitees)
}
