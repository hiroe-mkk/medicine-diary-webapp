package example.infrastructure.query.user

import example.application.query.shared.type.*
import example.domain.model.account.*
import org.apache.ibatis.annotations.*

@Mapper
interface UserMapper {
    fun findManyByKeyword(keyword: String, accountId: String): List<User>
}