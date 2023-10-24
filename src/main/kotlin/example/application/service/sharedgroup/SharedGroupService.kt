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
                         private val shareRequestService: ShareRequestService) {
    /**
     * 共有リクエストできるか
     */
    @Transactional(readOnly = true)
    fun canShareRequest(userSession: UserSession): Boolean {
        return shareRequestService.canShareRequest(userSession.accountId)
    }

    /**
     * 共有をリクエストする
     */
    fun requestToShare(target: AccountId, userSession: UserSession): SharedGroupId {
        shareRequestService.requireShareRequestPossibleState(userSession.accountId)

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
        val sharedGroup = findParticipatingSharedGroup(sharedGroupId, userSession)
                          ?: throw SharedGroupNotFoundException(sharedGroupId)

        sharedGroup.invite(target, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)
    }

    /**
     * 招待を拒否する
     */
    fun declineInvitation(sharedGroupId: SharedGroupId, userSession: UserSession) {
        val sharedGroup = findInvitedSharedGroup(sharedGroupId, userSession)
                          ?: throw SharedGroupNotFoundException(sharedGroupId)

        sharedGroup.declineInvitation(userSession.accountId)
        if (sharedGroup.shouldDelete()) {
            sharedGroupRepository.delete(sharedGroupId)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }

    private fun findParticipatingSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession): SharedGroup? {
        val sharedGroup = sharedGroupRepository.findById(sharedGroupId) ?: return null
        return if (sharedGroup.isParticipatingIn(userSession.accountId)) sharedGroup else null
    }

    private fun findInvitedSharedGroup(sharedGroupId: SharedGroupId, userSession: UserSession): SharedGroup? {
        val sharedGroup = sharedGroupRepository.findById(sharedGroupId) ?: return null
        return if (sharedGroup.isInvited(userSession.accountId)) sharedGroup else null
    }
}