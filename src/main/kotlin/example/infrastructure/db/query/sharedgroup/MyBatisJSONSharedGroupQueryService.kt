package example.infrastructure.db.query.sharedgroup

import example.application.query.sharedgroup.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONSharedGroupQueryService(private val jsonSharedGroupMapper: JSONSharedGroupMapper)
    : JSONSharedGroupQueryService {
    override fun findJSONJoinedSharedGroup(userSession: UserSession): JSONSharedGroup {
        return jsonSharedGroupMapper.findOneByMember(userSession.accountId.value)
    }
}
