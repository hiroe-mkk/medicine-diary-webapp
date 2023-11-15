package example.application.service.sharedgroup

import SharedGroupNotFoundException
import example.application.service.account.*
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
     * 共有する
     */
    fun share(target: AccountId, userSession: UserSession): SharedGroupId {
        sharedGroupParticipationService.requireSharePossible(userSession.accountId)
        val sharedGroup = SharedGroup.create(sharedGroupRepository.createSharedGroupId(), userSession.accountId)

        requireAccountExists(target)
        sharedGroup.invite(target, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup.id
    }

    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupId: SharedGroupId, target: AccountId, userSession: UserSession) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(sharedGroupId, userSession.accountId)
                          ?: throw SharedGroupNotFoundException(sharedGroupId)
        requireAccountExists(target)

        sharedGroup.invite(target, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有グループに参加する
     */
    fun participateInSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = sharedGroupQueryService.findInvitedSharedGroup(sharedGroupId, userSession.accountId)
                          ?: throw SharedGroupNotFoundException(sharedGroupId)
        sharedGroupParticipationService.requireParticipationPossible(userSession.accountId)

        sharedGroup.participateIn(userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有グループへの招待を拒否する
     */
    fun rejectInvitationToSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = sharedGroupQueryService.findInvitedSharedGroup(sharedGroupId,
                                                                         userSession.accountId) ?: return

        sharedGroup.rejectInvitation(userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有グループへの招待を取り消す
     */
    fun cancelInvitationToSharedGroup(sharedGroupId: SharedGroupId, target: AccountId, userSession: UserSession) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(sharedGroupId,
                                                                               userSession.accountId) ?: return

        sharedGroup.cancelInvitation(target)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有を解除する
     */
    fun unshare(sharedGroupId: SharedGroupId, userSession: UserSession) {
        return sharedGroupUnshareService.unshare(sharedGroupId, userSession.accountId)
    }

    /**
     * 参加している共有グループか
     */
    fun isParticipatingInSharedGroup(userSession: UserSession): Boolean {
        return sharedGroupParticipationService.isParticipatingInSharedGroup(userSession.accountId)
    }

    private fun requireAccountExists(target: AccountId) {
        accountRepository.findById(target) ?: throw AccountNotFoundException(target)
    }
}