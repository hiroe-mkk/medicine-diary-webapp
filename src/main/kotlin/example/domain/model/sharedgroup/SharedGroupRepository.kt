package example.domain.model.sharedgroup

import example.domain.model.account.*

interface SharedGroupRepository {
    fun findById(sharedGroupId: SharedGroupId): SharedGroup?

    fun findByAccountId(accountId: AccountId): Set<SharedGroup>

    fun save(sharedGroup: SharedGroup)

    fun delete(sharedGroupId: SharedGroupId)
}