package example.infrastructure.repository.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

class SharedGroupResultEntity(val sharedGroupId: SharedGroupId) {
    val members: Set<AccountId> = mutableSetOf()
    val invitees: Set<AccountId> = mutableSetOf()

    fun toSharedGroup(): SharedGroup = SharedGroup(sharedGroupId, members, invitees)
}