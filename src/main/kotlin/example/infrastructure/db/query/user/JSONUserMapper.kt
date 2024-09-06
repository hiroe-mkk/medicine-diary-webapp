package example.infrastructure.db.query.user

import example.application.query.user.*
import org.apache.ibatis.annotations.*

@Mapper
interface JSONUserMapper {
    fun findOneByAccountId(accountId: String): JSONUser?

    fun findAllBySharedGroupId(sharedGroupId: String, accountId: String): List<JSONUser>
}
