package example.infrastructure.db.repository.sharedgroup

import example.domain.model.sharedgroup.*
import org.apache.ibatis.annotations.*

@Mapper
interface SharedGroupMapper {
    fun findOneBySharedGroupId(sharedGroupId: String): SharedGroupResultEntity?

    fun findOneByMember(accountId: String): SharedGroupResultEntity?

    fun findOneByInviteCode(inviteCode: String): SharedGroupResultEntity?

    fun insertSharedGroup(sharedGroupId: String)

    fun insertAllMembers(sharedGroupId: String, members: Collection<String>)

    fun insertAllPendingInvitations(sharedGroupId: String, pendingInvitations: Collection<PendingInvitation>)

    fun deleteSharedGroup(sharedGroupId: String)

    fun deleteAllMembers(sharedGroupId: String)

    fun deleteAllPendingInvitations(sharedGroupId: String)
}
