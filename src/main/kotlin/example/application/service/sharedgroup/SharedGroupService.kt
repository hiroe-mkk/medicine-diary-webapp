package example.application.service.sharedgroup

import SharedGroupNotFoundException
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupService(private val sharedGroupRepository: SharedGroupRepository,
                         private val sharedGroupDomainService: SharedGroupDomainService) {
    /**
     * 共有する
     */
    fun share(target: AccountId, userSession: UserSession): SharedGroupId {
        sharedGroupDomainService.requireShareableState(userSession.accountId)

        val sharedGroup = SharedGroup.create(sharedGroupRepository.createSharedGroupId(),
                                             userSession.accountId,
                                             target)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup.id
    }

    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupId: SharedGroupId, target: AccountId, userSession: UserSession) {
        val sharedGroup = findParticipatingSharedGroupOrElseThrowException(sharedGroupId, userSession)

        sharedGroup.invite(target, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有グループへの招待を拒否する
     */
    fun declineInvitationToSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = findInvitedSharedGroupOrElseThrowException(sharedGroupId, userSession)

        sharedGroup.declineInvitation(userSession.accountId)
        if (sharedGroup.shouldDelete()) {
            sharedGroupRepository.delete(sharedGroupId)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }

    /**
     * 共有グループへの招待をキャンセルする
     */
    fun cancelInvitationToSharedGroup(sharedGroupId: SharedGroupId, target: AccountId, userSession: UserSession) {
        val sharedGroup = findParticipatingSharedGroupOrElseThrowException(sharedGroupId, userSession)

        sharedGroup.cancelInvitation(target)
        if (sharedGroup.shouldDelete()) {
            sharedGroupRepository.delete(sharedGroupId)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }

    /**
     * 共有グループに参加する
     */
    fun participateInSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = findInvitedSharedGroupOrElseThrowException(sharedGroupId, userSession)
        sharedGroupDomainService.requireParticipationPossibleState(userSession.accountId)

        sharedGroup.participateIn(userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 共有グループから抜ける
     */
    fun leaveSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = findParticipatingSharedGroupOrElseThrowException(sharedGroupId, userSession)

        sharedGroup.leave(userSession.accountId)
        if (sharedGroup.shouldDelete()) {
            sharedGroupRepository.delete(sharedGroupId)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }

    private fun findParticipatingSharedGroupOrElseThrowException(sharedGroupId: SharedGroupId,
                                                                 userSession: UserSession): SharedGroup {
        return sharedGroupRepository.findById(sharedGroupId)
                   ?.let { if (it.isParticipatingIn(userSession.accountId)) it else null }
               ?: throw SharedGroupNotFoundException(sharedGroupId)
    }

    private fun findInvitedSharedGroupOrElseThrowException(sharedGroupId: SharedGroupId,
                                                           userSession: UserSession): SharedGroup {
        return sharedGroupRepository.findById(sharedGroupId)
                   ?.let { if (it.isInvited(userSession.accountId)) it else null }
               ?: throw SharedGroupNotFoundException(sharedGroupId)
    }
}