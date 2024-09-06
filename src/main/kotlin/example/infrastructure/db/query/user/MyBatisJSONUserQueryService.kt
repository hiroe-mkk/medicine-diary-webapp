package example.infrastructure.db.query.user

import example.application.query.user.*
import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONUserQueryService(private val jsonUserMapper: JSONUserMapper) : JSONUserQueryService {
    override fun findJSONUser(accountId: AccountId): JSONUser {
        return jsonUserMapper.findOneByAccountId(accountId.value) ?: throw AccountNotFoundException(accountId)
    }

    override fun findJSONSharedGroupMember(sharedGroupId: SharedGroupId, userSession: UserSession): JSONUsers {
        return JSONUsers(jsonUserMapper.findAllBySharedGroupId(sharedGroupId.value, userSession.accountId.value))
    }
}
