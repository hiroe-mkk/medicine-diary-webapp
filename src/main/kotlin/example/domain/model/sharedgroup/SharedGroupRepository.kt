package example.domain.model.sharedgroup

import example.domain.model.account.*

interface SharedGroupRepository {
    fun createSharedGroupId(): SharedGroupId

    fun findById(sharedGroupId: SharedGroupId): SharedGroup?

    fun findByMember(accountId: AccountId): SharedGroup?

    fun findByInvitee(accountId: AccountId): Set<SharedGroup>

    fun save(sharedGroup: SharedGroup)

    fun deleteById(sharedGroupId: SharedGroupId)
}