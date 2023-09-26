package example.presentation.shared.usersession

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.presentation.shared.springframework.security.*
import org.springframework.stereotype.*

@Component
class UserSessionProvider(private val authenticatedAccountProvider: AuthenticatedAccountProvider) {
    fun getUserSession(): UserSession? {
        return authenticatedAccountProvider.getAuthenticatedAccount()
            ?.let { SpringSecurityUserSession(it.id) }
    }

    private data class SpringSecurityUserSession(override val accountId: AccountId) : UserSession
}