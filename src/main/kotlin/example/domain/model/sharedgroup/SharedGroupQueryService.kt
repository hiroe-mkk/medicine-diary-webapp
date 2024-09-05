package example.domain.model.sharedgroup

import example.domain.model.account.*
import org.springframework.stereotype.*

@Component
class SharedGroupQueryService(private val sharedGroupRepository: SharedGroupRepository) {
    fun findJoinedSharedGroup(accountId: AccountId): SharedGroup? {
        return sharedGroupRepository.findByMember(accountId)
    }

    fun findInvitedSharedGroups(accountId: AccountId): Set<SharedGroup> {
        return sharedGroupRepository.findByInvitee(accountId)
    }

    fun isJoinedSharedGroup(accountId: AccountId): Boolean {
        return findJoinedSharedGroup(accountId) != null
    }
}
