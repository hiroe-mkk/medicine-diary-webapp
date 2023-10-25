package example.infrastructure.query.sharedgroup

import example.application.query.shared.type.*
import example.application.query.sharedgroup.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisSharedGroupQueryService(private val sharedGroupDetailMapper: SharedGroupDetailMapper)
    : SharedGroupQueryService {
    override fun findSharedGroupDetails(userSession: UserSession): SharedGroups {
        val sharedGroups = sharedGroupDetailMapper.findAllByAccountId(userSession.accountId.value)
            .map { it.toSharedGroupDetail() }
        val participatingSharedGroup = getParticipatingSharedGroup(sharedGroups, userSession)
        val invitedSharedGroup = invitedSharedGroupDetails(participatingSharedGroup, sharedGroups).toSet()
        return SharedGroups(participatingSharedGroup, invitedSharedGroup)
    }

    private fun getParticipatingSharedGroup(sharedGroups: Collection<SharedGroupDetail>,
                                            userSession: UserSession): SharedGroupDetail? {
        return sharedGroups.find { it.members.map(User::accountId).contains(userSession.accountId) }
    }

    private fun invitedSharedGroupDetails(participatingSharedGroup: SharedGroupDetail?,
                                          sharedGroups: Collection<SharedGroupDetail>): Collection<SharedGroupDetail> {
        return if (participatingSharedGroup == null) sharedGroups else sharedGroups - participatingSharedGroup
    }
}