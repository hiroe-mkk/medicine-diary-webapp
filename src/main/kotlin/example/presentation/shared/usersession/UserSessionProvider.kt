package example.presentation.shared.usersession

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.presentation.shared.springframework.security.*
import org.springframework.stereotype.*

@Component
class UserSessionProvider(private val authenticatedAccountProvider: AuthenticatedAccountProvider) {
    fun getUserSessionOrElseThrow(): UserSession {
        val authenticatedAccount = authenticatedAccountProvider.getAuthenticatedAccount()
                                   ?: throw UserSessionNotFoundException()
        return SpringSecurityUserSession(authenticatedAccount.id)
    }

    fun getUserSession(): UserSession? {
        val authenticatedAccount = authenticatedAccountProvider.getAuthenticatedAccount() ?: return null
        return SpringSecurityUserSession(authenticatedAccount.id)
    }

    private data class SpringSecurityUserSession(override val accountId: AccountId) : UserSession
}