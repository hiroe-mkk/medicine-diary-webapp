package example.infrastructure.repository.sharedgroup

import org.apache.ibatis.annotations.*

@Mapper
interface SharedGroupMapper {
    fun findOneBySharedGroupId(sharedGroupId: String): SharedGroupResultEntity?

    fun findAllByAccountId(accountId: String): Collection<SharedGroupResultEntity>

    fun insertOneSharedGroup(sharedGroupId: String)

    fun insertAllMembers(sharedGroupId: String, members: Collection<String>)

    fun insertAllInvitees(sharedGroupId: String, invitees: Collection<String>)

    fun deleteOneSharedGroup(sharedGroupId: String)

    fun deleteAllMembers(sharedGroupId: String)

    fun deleteAllInvitees(sharedGroupId: String)
}