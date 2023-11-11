package example.infrastructure.query.user

import example.application.query.shared.type.*
import org.apache.ibatis.annotations.*

@Mapper
interface UserMapper {
    fun findManyByKeyword(keyword: String, accountId: String): List<User>

    fun findAllUserMembers(accountId: String): List<User>
}