package example.domain.model.sharedgroup

import example.domain.model.account.*
import org.springframework.stereotype.*
import java.time.*

@Component
class SharedGroupFinder(private val sharedGroupRepository: SharedGroupRepository) {
    fun findInvitedSharedGroup(inviteCode: String, date: LocalDate): SharedGroup? {
        val sharedGroup = sharedGroupRepository.findByInviteCode(inviteCode) ?: return null
        return if (sharedGroup.isValidInviteCode(inviteCode, date)) sharedGroup else null
    }

    fun findJoinedSharedGroup(accountId: AccountId): SharedGroup? {
        return sharedGroupRepository.findByMember(accountId)
    }

    fun isJoinedSharedGroup(accountId: AccountId): Boolean {
        return findJoinedSharedGroup(accountId) != null
    }
}
