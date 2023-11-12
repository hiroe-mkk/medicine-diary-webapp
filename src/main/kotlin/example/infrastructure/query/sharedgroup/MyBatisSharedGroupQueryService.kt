package example.infrastructure.query.sharedgroup

import example.application.query.sharedgroup.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisSharedGroupQueryService(private val jsonSharedGroupMapper: JSONSharedGroupMapper)
    : SharedGroupQueryService {
    override fun findSharedGroup(userSession: UserSession): JSONSharedGroups {
        val sharedGroups = jsonSharedGroupMapper.findAllByAccountId(userSession.accountId.value)
        val participatingSharedGroup = extractingParticipatingSharedGroup(sharedGroups, userSession)
        val invitedSharedGroup = invitedSharedGroups(participatingSharedGroup, sharedGroups)
        return JSONSharedGroups(participatingSharedGroup, invitedSharedGroup)
    }

    private fun extractingParticipatingSharedGroup(sharedGroups: Collection<JSONSharedGroup>,
                                                   userSession: UserSession): JSONSharedGroup? {
        return sharedGroups.find { it.members.map { AccountId(it.accountId) }.contains(userSession.accountId) }
    }

    private fun invitedSharedGroups(participatingSharedGroup: JSONSharedGroup?,
                                    sharedGroups: Collection<JSONSharedGroup>): Set<JSONSharedGroup> {
        return if (participatingSharedGroup == null) {
            sharedGroups.toSet()
        } else {
            (sharedGroups - participatingSharedGroup).toSet()
        }
    }
}