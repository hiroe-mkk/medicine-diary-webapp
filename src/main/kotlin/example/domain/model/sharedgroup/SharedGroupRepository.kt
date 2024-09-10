package example.domain.model.sharedgroup

import example.domain.model.account.*

interface SharedGroupRepository {
    fun createSharedGroupId(): SharedGroupId

    fun createInviteCode(): String

    fun findById(sharedGroupId: SharedGroupId): SharedGroup?

    fun findByInviteCode(inviteCode: String): SharedGroup?

    fun findByMember(accountId: AccountId): SharedGroup?

    fun save(sharedGroup: SharedGroup)

    fun deleteById(sharedGroupId: SharedGroupId)

    // TODO: 削除単位に問題ないか再考する
    fun deleteExpiredPendingInvitation()
}
