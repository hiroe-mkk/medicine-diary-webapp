package example.domain.model.sharedgroup

import example.domain.model.account.*

/**
 * 共有グループ
 */
class SharedGroup(val id: SharedGroupId,
                  members: Set<AccountId>,
                  pendingUsers: Set<AccountId>) {
    var members: Set<AccountId> = members
        private set
    var pendingUsers: Set<AccountId> = pendingUsers
        private set

    companion object {
        fun create(id: SharedGroupId, requester: AccountId, target: AccountId): SharedGroup {
            return SharedGroup(id, setOf(requester), setOf(target))
        }
    }

    fun invite(accountId: AccountId) {
        pendingUsers += accountId
    }

    fun participateIn(accountId: AccountId) {
        members += accountId
    }
}