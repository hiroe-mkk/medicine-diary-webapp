package example.presentation.shared.springframework.security.oauth2.oidc

import example.application.service.account.*
import example.domain.model.account.*
import org.springframework.security.oauth2.client.oidc.userinfo.*
import org.springframework.security.oauth2.core.oidc.user.*
import org.springframework.stereotype.*

@Component
class OidcUserAccountService(private val accountService: AccountService) : OidcUserService() {
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val oidcUser = super.loadUser(userRequest)
        val credential = OAuth2Credential(extractIdP(userRequest), extractSubject(oidcUser))
        val account = accountService.findOrElseCreateAccount(credential)
        return OidcUserAccount(account.id, oidcUser)
    }

    private fun extractSubject(oidcUser: OidcUser): String = oidcUser.attributes["sub"] as String

    private fun extractIdP(userRequest: OidcUserRequest): IdP = IdP.from(userRequest.clientRegistration.registrationId)
}