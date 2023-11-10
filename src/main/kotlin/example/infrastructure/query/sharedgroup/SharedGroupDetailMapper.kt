package example.infrastructure.query.sharedgroup

import example.application.query.shared.type.*
import org.apache.ibatis.annotations.*

@Mapper
interface SharedGroupDetailMapper {
    fun findAllByAccountId(accountId: String): Set<SharedGroupDetailResultEntity>

    fun findAllParticipatingSharedGroupMembersByAccountId(accountId: String): Set<User>
}