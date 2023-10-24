package example.application.service.sharedgroup

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
}