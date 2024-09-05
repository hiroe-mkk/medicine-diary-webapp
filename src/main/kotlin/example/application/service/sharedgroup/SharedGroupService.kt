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
                         private val sharedGroupParticipationService: SharedGroupParticipationService,
                         private val sharedGroupUnshareService: SharedGroupUnshareService) {
    /**
     * 共有グループを作る
     */
    fun createSharedGroup(target: AccountId, userSession: UserSession): SharedGroupId {
        accountRepository.findById(target) ?: throw ShareException("ユーザーが見つかりませんでした。")
        sharedGroupParticipationService.requireSharePossible(userSession.accountId)

        val sharedGroup = SharedGroup.create(sharedGroupRepository.createSharedGroupId(), userSession.accountId)
        sharedGroup.invite(target, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup.id
    }

    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupId: SharedGroupId, target: AccountId, userSession: UserSession) {
        accountRepository.findById(target) ?: throw InvitationToSharedGroupException("ユーザーが見つかりませんでした。")
        val participatingSharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(userSession.accountId)
                                           ?.let { if (it.id == sharedGroupId) it else null }
                                       ?: throw InvitationToSharedGroupException("参加していない共有グループへの招待はできません。")

        participatingSharedGroup.invite(target, userSession.accountId)
        sharedGroupRepository.save(participatingSharedGroup)
    }

    /**
     * 共有グループに参加する
     */
    fun participateInSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val invitedSharedGroup = sharedGroupQueryService.findInvitedSharedGroups(userSession.accountId)
                                     .find { it.id == sharedGroupId }
                                 ?: throw ParticipationInSharedGroupException("招待されていない共有グループへの参加はできません")
        sharedGroupParticipationService.requireParticipationPossible(userSession.accountId)

        invitedSharedGroup.participateIn(userSession.accountId)
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
     * 共有グループへの招待を取り消す
     */
    fun cancelInvitationToSharedGroup(sharedGroupId: SharedGroupId, target: AccountId, userSession: UserSession) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(userSession.accountId)
                              ?.let { if (it.id == sharedGroupId) it else null } ?: return

        sharedGroup.cancelInvitation(target)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有を停止する
     */
    fun unshare(userSession: UserSession) {
        return sharedGroupUnshareService.cloneSharedGroupMedicinesAndUnshare(userSession.accountId)
    }

    /**
     * 共有グループに参加しているか
     */
    fun isParticipatingInSharedGroup(userSession: UserSession): Boolean {
        return sharedGroupQueryService.isParticipatingInSharedGroup(userSession.accountId)
    }
}
