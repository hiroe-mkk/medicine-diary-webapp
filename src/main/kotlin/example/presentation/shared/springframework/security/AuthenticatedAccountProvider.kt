package example.presentation.shared.springframework.security

import org.springframework.security.core.context.*
import org.springframework.stereotype.*

@Component
class AuthenticatedAccountProvider {
    fun getAuthenticatedAccount(): AuthenticatedAccount? {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal as? AuthenticatedAccount
    }
}