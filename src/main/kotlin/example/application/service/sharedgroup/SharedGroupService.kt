package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupService(private val sharedGroupRepository: SharedGroupRepository,
                         private val accountRepository: AccountRepository,
                         private val sharedGroupQueryService: SharedGroupQueryService,
                         private val sharedGroupJoinService: SharedGroupJoinService,
                         private val sharedGroupLeaveService: SharedGroupLeaveService) {
    /**
     * 共有グループを作る
     */
    fun createSharedGroup(invitee: AccountId, userSession: UserSession): SharedGroupId {
        accountRepository.findById(invitee)
        ?: throw SharedGroupCreationFailedException("ユーザーが見つかりませんでした。")
        sharedGroupJoinService.requireSharePossible(userSession.accountId)

        val sharedGroup = SharedGroup.create(sharedGroupRepository.createSharedGroupId(), userSession.accountId)

        sharedGroup.invite(invitee, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup.id
    }

    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupId: SharedGroupId, invitee: AccountId, userSession: UserSession) {
        accountRepository.findById(invitee) ?: throw SharedGroupInviteFailedException("ユーザーが見つかりませんでした。")
        val joinedSharedGroup = sharedGroupQueryService.findJoinedSharedGroup(userSession.accountId)
                                    ?.let { if (it.id == sharedGroupId) it else null }
                                ?: throw SharedGroupInviteFailedException("参加していない共有グループへの招待はできません。")

        joinedSharedGroup.invite(invitee, userSession.accountId)
        sharedGroupRepository.save(joinedSharedGroup)
    }

    /**
     * 共有グループに参加する
     */
    fun joinSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val invitedSharedGroup = sharedGroupQueryService.findInvitedSharedGroups(userSession.accountId)
                                     .find { it.id == sharedGroupId }
                                 ?: throw SharedGroupJoinFailedException("招待されていない共有グループへの参加はできません")
        sharedGroupJoinService.requireJoinPossible(userSession.accountId)

        invitedSharedGroup.join(userSession.accountId)
        sharedGroupRepository.save(invitedSharedGroup)
    }

    /**
     * 共有グループへの招待を拒否する
     */
    fun rejectInvitationToSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = sharedGroupQueryService.findInvitedSharedGroups(userSession.accountId)
                              .find { it.id == sharedGroupId } ?: return

        sharedGroup.rejectInvitation(userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有グループから脱退する
     */
    fun leaveSharedGroup(userSession: UserSession) {
        return sharedGroupLeaveService.leaveAndCloneMedicines(userSession.accountId)
    }

    /**
     * 共有グループに参加しているか
     */
    fun isJoinedSharedGroup(userSession: UserSession): Boolean {
        return sharedGroupQueryService.isJoinedSharedGroup(userSession.accountId)
    }
}
