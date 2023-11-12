package example.infrastructure.query.user

import example.application.query.shared.type.*
import example.application.query.user.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisUserQueryService(private val jsonUserMapper: JSONUserMapper) : UserQueryService {
    override fun findUser(userSession: UserSession): JSONUser {
        return jsonUserMapper.findOneByAccountId(userSession.accountId.value)
    }

    override fun findJSONUsersByKeyword(keyword: String, userSession: UserSession): JSONUsers {
        return JSONUsers(jsonUserMapper.findManyByKeyword(keyword, userSession.accountId.value))
    }

    override fun findMemberJSONUsers(userSession: UserSession): JSONUsers {
        return JSONUsers(jsonUserMapper.findAllMemberUsers(userSession.accountId.value))
    }
}