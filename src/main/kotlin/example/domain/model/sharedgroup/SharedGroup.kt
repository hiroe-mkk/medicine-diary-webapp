package example.domain.model.sharedgroup

import example.domain.model.account.*

/**
 * 共有グループ
 */
class SharedGroup(val id: SharedGroupId,
                  members: Set<AccountId>,
                  invitees: Set<AccountId>) {
    var members: Set<AccountId> = members
        private set
    var invitees: Set<AccountId> = invitees
        private set

    companion object {
        fun create(id: SharedGroupId, accountId: AccountId): SharedGroup {
            return SharedGroup(id, setOf(accountId), emptySet())
        }
    }

    fun isJoined(accountId: AccountId): Boolean = members.contains(accountId)

    fun isInvited(accountId: AccountId): Boolean = invitees.contains(accountId)

    fun shouldDelete(): Boolean {
        return members.isEmpty()
    }

    fun invite(invitee: AccountId, inviter: AccountId) {
        if (!isJoined(inviter)) throw InvitationToSharedGroupException("参加していない共有グループへの招待はできません。")
        if (isJoined(invitee)) throw InvitationToSharedGroupException("既に共有グループに参加しているユーザーです。")

        invitees += invitee
    }

    fun rejectInvitation(invitee: AccountId) {
        invitees -= invitee
    }

    fun cancelInvitation(invitee: AccountId) {
        invitees -= invitee
    }

    fun join(invitee: AccountId) {
        if (!isInvited(invitee)) throw ParticipationInSharedGroupException("招待されていない共有グループへの参加はできません。")

        invitees -= invitee
        members += invitee
    }

    fun leave(member: AccountId) {
        members -= member
    }
}
