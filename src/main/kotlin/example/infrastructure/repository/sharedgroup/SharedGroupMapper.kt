package example.infrastructure.repository.sharedgroup

import org.apache.ibatis.annotations.*

@Mapper
interface SharedGroupMapper {
    fun findOneBySharedGroupId(sharedGroupId: String): SharedGroupResultEntity?

    fun insertOneSharedGroup(sharedGroupId: String)

    fun insertAllMembers(sharedGroupId: String, members: Collection<String>)

    fun insertAllPendingUsers(sharedGroupId: String, pendingUsers: Collection<String>)

    fun deleteOneSharedGroup(sharedGroupId: String)

    fun deleteAllMembers(sharedGroupId: String)

    fun deleteAllPendingUsers(sharedGroupId: String)
}