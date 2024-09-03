package example.infrastructure.db.query.user

import example.application.query.user.*
import org.apache.ibatis.annotations.*

@Mapper
interface UserMapper {
    fun findOneMemberUserByAccountId(accountId: String, requester: String): User?
}
