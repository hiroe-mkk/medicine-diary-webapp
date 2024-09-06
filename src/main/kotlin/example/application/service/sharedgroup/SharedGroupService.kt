package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupService(private val sharedGroupRepository: SharedGroupRepository,
                         private val localDateTimeProvider: LocalDateTimeProvider,
                         private val sharedGroupLeaveService: SharedGroupLeaveService) {
    /**
     * 共有グループを作る
     */
    fun createSharedGroup(userSession: UserSession): SharedGroupId {
        if (isJoinedSharedGroup(userSession)) throw SharedGroupCreationFailedException("参加できる共有グループは1つまでです。")

        val sharedGroup = SharedGroup.create(sharedGroupRepository.createSharedGroupId(), userSession.accountId)
        val pendingInvitation = PendingInvitation(sharedGroupRepository.createInviteCode(),
                                                  localDateTimeProvider.today())
        sharedGroup.invite(pendingInvitation, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup.id
    }

    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val joinedSharedGroup = sharedGroupRepository.findByMember(userSession.accountId)
                                    ?.let { if (it.id == sharedGroupId) it else null }
                                ?: throw SharedGroupInviteFailedException("参加していない共有グループへの招待はできません。")

        val pendingInvitation = PendingInvitation(sharedGroupRepository.createInviteCode(),
                                                  localDateTimeProvider.today())
        joinedSharedGroup.invite(pendingInvitation, userSession.accountId)
        sharedGroupRepository.save(joinedSharedGroup)
    }

    /**
     * 共有グループに参加する
     */
    fun joinSharedGroup(inviteCode: String, userSession: UserSession) {
        if (isJoinedSharedGroup(userSession)) throw SharedGroupJoinFailedException("参加できる共有グループは1つまでです。")
        val invitedSharedGroup = sharedGroupRepository.findByInviteCode(inviteCode)
                                 ?: throw SharedGroupJoinFailedException("現在、このグループからは招待されていません。")

        invitedSharedGroup.join(inviteCode, userSession.accountId, localDateTimeProvider.today())
        sharedGroupRepository.save(invitedSharedGroup)
    }

    /**
     * 共有グループへの招待を拒否する
     */
    fun rejectInvitationToSharedGroup(inviteCode: String, userSession: UserSession) {
        val invitedSharedGroup = sharedGroupRepository.findByInviteCode(inviteCode) ?: return

        invitedSharedGroup.reject(inviteCode)
        sharedGroupRepository.save(invitedSharedGroup)
    }

    /**
     * 共有グループから脱退する
     */
    fun leaveSharedGroup(userSession: UserSession) {
        return sharedGroupLeaveService.leaveSharedGroupAndCloneMedicines(userSession.accountId)
    }

    /**
     * 参加している共有グループの ID を取得する
     */
    fun getJoinedSharedGroup(userSession: UserSession): SharedGroupId? {
        return sharedGroupRepository.findByMember(userSession.accountId)?.id
    }

    /**
     * 共有グループに参加しているか
     */
    fun isJoinedSharedGroup(userSession: UserSession): Boolean {
        return getJoinedSharedGroup(userSession) != null
    }
}
