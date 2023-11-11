package example.infrastructure.query.sharedgroup

import example.application.query.sharedgroup.*
import org.apache.ibatis.annotations.*

@Mapper
interface DisplaySharedGroupMapper {
    fun findAllByAccountId(accountId: String): Set<DisplaySharedGroup>
}