package example.domain.model.sharedgroup

import example.domain.model.account.*
import org.springframework.stereotype.*

@Component
class SharedGroupQueryService(private val sharedGroupRepository: SharedGroupRepository) {
    fun findParticipatingSharedGroup(sharedGroupId: SharedGroupId, accountId: AccountId): SharedGroup? {
        val sharedGroup = findSharedGroup(sharedGroupId) ?: return null
        return if (sharedGroup.isParticipatingIn(accountId)) sharedGroup else null
    }

    fun findInvitedSharedGroup(sharedGroupId: SharedGroupId, accountId: AccountId): SharedGroup? {
        val sharedGroup = findSharedGroup(sharedGroupId) ?: return null
        return if (sharedGroup.isInvited(accountId)) sharedGroup else null
    }

    private fun findSharedGroup(sharedGroupId: SharedGroupId) = sharedGroupRepository.findById(sharedGroupId)
}