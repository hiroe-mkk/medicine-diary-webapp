package example.infrastructure.db.query.user

import example.application.query.user.*
import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONUserQueryService(private val jsonUserMapper: JSONUserMapper) : JSONUserQueryService {
    override fun findJSONUser(accountId: AccountId): JSONUser {
        return jsonUserMapper.findOneByAccountId(accountId.value) ?: throw AccountNotFoundException(accountId)
    }

    override fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers {
        if (keyword.isBlank()) return JSONUsers(emptyList())
        return JSONUsers(jsonUserMapper.findManyByKeyword(keyword, userSession.accountId.value))
    }

    override fun findJSONMemberUsers(userSession: UserSession): JSONUsers {
        return JSONUsers(jsonUserMapper.findAllMemberUsers(userSession.accountId.value))
    }
}
