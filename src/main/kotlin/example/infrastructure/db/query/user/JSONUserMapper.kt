package example.infrastructure.db.query.user

import example.application.query.user.*
import org.apache.ibatis.annotations.*

@Mapper
interface JSONUserMapper {
    fun findAllBySharedGroupId(sharedGroupId: String): List<JSONUser>
}
