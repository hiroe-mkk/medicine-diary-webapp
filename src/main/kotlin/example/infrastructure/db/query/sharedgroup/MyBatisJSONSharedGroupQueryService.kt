package example.infrastructure.db.query.sharedgroup

import example.application.query.sharedgroup.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONSharedGroupQueryService(private val jsonSharedGroupMapper: example.infrastructure.db.query.sharedgroup.JSONSharedGroupMapper)
    : JSONSharedGroupQueryService {
    override fun findJSONSharedGroup(userSession: UserSession): JSONSharedGroups {
        val sharedGroups = jsonSharedGroupMapper.findAllByAccountId(userSession.accountId.value)
        val joinedSharedGroup = extractingJoinedSharedGroup(sharedGroups, userSession)
        val invitedSharedGroup = invitedSharedGroups(joinedSharedGroup, sharedGroups)
        return JSONSharedGroups(joinedSharedGroup, invitedSharedGroup)
    }

    private fun extractingJoinedSharedGroup(sharedGroups: Collection<JSONSharedGroup>,
                                            userSession: UserSession): JSONSharedGroup? {
        return sharedGroups.find { it.members.map { AccountId(it.accountId) }.contains(userSession.accountId) }
    }

    private fun invitedSharedGroups(joinedSharedGroup: JSONSharedGroup?,
                                    sharedGroups: Collection<JSONSharedGroup>): Set<JSONSharedGroup> {
        return if (joinedSharedGroup == null) {
            sharedGroups.toSet()
        } else {
            (sharedGroups - joinedSharedGroup).toSet()
        }
    }
}
