package example.infrastructure.query.sharedgroup

import org.apache.ibatis.annotations.*

@Mapper
interface SharedGroupDetailMapper {
    fun findAllByAccountId(accountId: String): Set<SharedGroupDetailResultEntity>
}