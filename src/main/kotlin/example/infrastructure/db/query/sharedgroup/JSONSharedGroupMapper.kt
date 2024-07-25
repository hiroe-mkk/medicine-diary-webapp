package example.infrastructure.db.query.sharedgroup

import example.application.query.sharedgroup.*
import org.apache.ibatis.annotations.*

@Mapper
interface JSONSharedGroupMapper {
    fun findAllByAccountId(accountId: String): Set<JSONSharedGroup>
}
