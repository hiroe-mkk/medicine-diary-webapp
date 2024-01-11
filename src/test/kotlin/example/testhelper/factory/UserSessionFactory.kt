package example.testhelper.factory

import example.application.shared.usersession.*
import example.domain.model.account.*

object UserSessionFactory {
    fun create(accountId: AccountId): UserSession {
        return MockUserSession(accountId)
    }

    class MockUserSession(override val accountId: AccountId) : UserSession
}