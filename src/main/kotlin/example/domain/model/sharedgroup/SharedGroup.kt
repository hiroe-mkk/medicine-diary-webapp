package example.domain.model.sharedgroup

import example.domain.model.account.*
import java.time.*

/**
 * 共有グループ
 */
class SharedGroup(val id: SharedGroupId,
                  members: Set<AccountId>,
                  pendingInvitations: Set<PendingInvitation>) {
    var members: Set<AccountId> = members
        private set
    var pendingInvitations: Set<PendingInvitation> = pendingInvitations
        private set

    private fun isJoined(accountId: AccountId): Boolean = members.contains(accountId)

    private fun getPendingInvitation(inviteCode: String) = pendingInvitations.find { it.inviteCode == inviteCode }

    fun validateInviteCode(inviteCode: String, requester: AccountId, date: LocalDate) {
        if (isJoined(requester))
            throw InvalidInvitationException(inviteCode, "すでにこのグループに参加しています。")
        val pendingInvitation = getPendingInvitation(inviteCode)
                                ?: throw InvalidInvitationException(inviteCode, "招待が確認できません。")
        if (!pendingInvitation.isValid(date))
            throw InvalidInvitationException(inviteCode, "有効期限が切れています。")
    }

    companion object {
        fun create(id: SharedGroupId, accountId: AccountId): SharedGroup {
            return SharedGroup(id, setOf(accountId), emptySet())
        }
    }

    fun shouldDelete(): Boolean {
        return members.isEmpty()
    }

    fun invite(pendingInvitation: PendingInvitation, requester: AccountId) {
        if (!isJoined(requester)) throw SharedGroupInviteFailedException("参加していない共有グループへの招待はできません。")

        pendingInvitations += pendingInvitation
    }

    fun reject(inviteCode: String) {
        val pendingInvitation = getPendingInvitation(inviteCode) ?: return

        pendingInvitations -= pendingInvitation
    }

    fun join(inviteCode: String, requester: AccountId, date: LocalDate) {
        validateInviteCode(inviteCode, requester, date)

        pendingInvitations -= getPendingInvitation(inviteCode)!!
        members += requester
    }

    fun leave(requester: AccountId) {
        members -= requester
    }
}
