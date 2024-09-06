package example.infrastructure.db.query.user

import example.application.query.user.*
import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisUserQueryService(private val userMapper: UserMapper) : UserQueryService {
    override fun findSharedGroupMember(accountId: AccountId, userSession: UserSession): User {
        return userMapper.findOneSharedGroupMemberByAccountId(accountId.value,
                                                              userSession.accountId.value)
               ?: throw AccountNotFoundException(accountId)
    }
}
