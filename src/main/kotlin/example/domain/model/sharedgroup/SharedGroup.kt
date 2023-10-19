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

    fun join(accountId: AccountId) {
        members += accountId
    }

    fun invite(accountId: AccountId) {
        pendingUsers += accountId
    }
}