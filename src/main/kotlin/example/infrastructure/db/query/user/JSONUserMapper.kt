package example.infrastructure.db.query.user

import example.application.query.user.*
import org.apache.ibatis.annotations.*

@Mapper
interface JSONUserMapper {
    fun findOneByAccountId(accountId: String): JSONUser?

    fun findManyByKeyword(keyword: String, accountId: String): List<JSONUser>

    fun findAllMemberUsers(accountId: String): List<JSONUser>
}
