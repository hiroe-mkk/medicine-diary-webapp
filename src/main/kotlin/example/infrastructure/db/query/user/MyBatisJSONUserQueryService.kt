package example.infrastructure.db.query.user

import example.application.query.user.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONUserQueryService(private val jsonUserMapper: JSONUserMapper) : JSONUserQueryService {
    override fun findJSONSharedGroupMember(sharedGroupId: SharedGroupId): JSONUsers {
        return JSONUsers(jsonUserMapper.findAllBySharedGroupId(sharedGroupId.value))
    }
}
