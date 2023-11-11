package example.infrastructure.query.sharedgroup

import example.application.query.shared.type.*
import example.application.query.sharedgroup.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisSharedGroupQueryService(private val displaySharedGroupMapper: DisplaySharedGroupMapper)
    : SharedGroupQueryService {
    override fun findSharedGroup(userSession: UserSession): SharedGroups {
        val sharedGroups = displaySharedGroupMapper.findAllByAccountId(userSession.accountId.value)
        val participatingSharedGroup = extractingParticipatingSharedGroup(sharedGroups, userSession)
        val invitedSharedGroup = invitedSharedGroups(participatingSharedGroup, sharedGroups)
        return SharedGroups(participatingSharedGroup, invitedSharedGroup)
    }

    private fun extractingParticipatingSharedGroup(sharedGroups: Collection<DisplaySharedGroup>,
                                                   userSession: UserSession): DisplaySharedGroup? {
        return sharedGroups.find { it.members.map(User::accountId).contains(userSession.accountId) }
    }

    private fun invitedSharedGroups(participatingSharedGroup: DisplaySharedGroup?,
                                    sharedGroups: Collection<DisplaySharedGroup>): Set<DisplaySharedGroup> {
        return if (participatingSharedGroup == null) {
            sharedGroups.toSet()
        } else {
            (sharedGroups - participatingSharedGroup).toSet()
        }
    }
}