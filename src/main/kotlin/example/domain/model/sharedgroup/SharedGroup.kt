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

    fun isParticipatingIn(accountId: AccountId): Boolean = members.contains(accountId)

    fun isInvited(accountId: AccountId): Boolean = pendingUsers.contains(accountId)

    fun shouldDelete(): Boolean = members.size + pendingUsers.size <= 1

    fun invite(invitee: AccountId, inviter: AccountId) {
        requireInvitableState(invitee, inviter)
        pendingUsers += invitee
    }

    private fun requireInvitableState(invitee: AccountId, inviter: AccountId) {
        if (!isParticipatingIn(inviter)) throw InvitationToSharedGroupFailedException("参加していない共有グループへの招待はできません。")
        if (isParticipatingIn(invitee)) throw InvitationToSharedGroupFailedException("既に共有グループに参加しているユーザーです。")
        if (isInvited(invitee)) throw InvitationToSharedGroupFailedException("既に共有グループに招待されているユーザーです。")
    }

    fun declineInvitation(accountId: AccountId) {
        pendingUsers -= accountId
    }

    fun cancelInvitation(accountId: AccountId) {
        pendingUsers -= accountId
    }

    fun participateIn(accountId: AccountId) {
        if (!isInvited(accountId)) throw ParticipationInSharedGroupFailedException("招待されていない共有グループへの参加はできません。")

        pendingUsers -= accountId
        members += accountId
    }

    fun leave(accountId: AccountId) {
        members -= accountId
    }
}