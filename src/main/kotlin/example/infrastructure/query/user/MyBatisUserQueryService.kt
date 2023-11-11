package example.infrastructure.query.user

import example.application.query.shared.type.*
import example.application.query.user.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional
class MyBatisUserQueryService(private val userMapper: UserMapper) : UserQueryService {
    override fun findByKeyword(keyword: String, userSession: UserSession): List<User> {
        return userMapper.findManyByKeyword(keyword, userSession.accountId.value)
    }

    override fun findMemberUsers(userSession: UserSession): List<User> {
        return userMapper.findAllUserMembers(userSession.accountId.value)
    }
}