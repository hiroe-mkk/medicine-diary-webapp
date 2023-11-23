package example.infrastructure.query.user

import example.application.query.user.*
import example.domain.model.account.*
import org.apache.ibatis.annotations.*

@Mapper
interface UserMapper {
    fun findOneMemberUserByAccountId(accountId: String, requester: String): User?
}