package example.domain.model.sharedgroup

import example.domain.model.account.*
import org.springframework.stereotype.*

@Component
class SharedGroupQueryService(private val sharedGroupRepository: SharedGroupRepository) {
    fun findParticipatingSharedGroup(accountId: AccountId): SharedGroup? {
        return sharedGroupRepository.findByMember(accountId)
    }

    fun findInvitedSharedGroups(accountId: AccountId): Set<SharedGroup> {
        return sharedGroupRepository.findByInvitee(accountId)
    }
}